package com.skiwi.githubhooksechatservice.init;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;
import com.skiwi.githubhooksechatservice.events.github.CreateEvent;
import com.skiwi.githubhooksechatservice.model.Followed;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubBean;
import com.skiwi.githubhooksechatservice.mvc.beans.RepositoryStats;
import com.skiwi.githubhooksechatservice.mvc.beans.Statistics;
import com.skiwi.githubhooksechatservice.mvc.controllers.GithubHookController;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;
import com.skiwi.githubhooksechatservice.service.ConfigService;
import com.skiwi.githubhooksechatservice.service.GithubService;

@Configuration
@EnableScheduling
public class ScheduledTasks {
    private static final Logger logger = Logger.getLogger(ScheduledTasks.class.getSimpleName());
	
    @Autowired
    private ChatBot chatBot;
    
    @Autowired
    private Statistics statistics;
    
    @Autowired
    private ConfigService configService;

    @Autowired
    private GithubService githubService;

    @Autowired
    private GithubBean githubBean;
    
    @Autowired
    private GithubHookController controller;

    @Scheduled(cron = "0 * * * * *") // second minute hour day day day
    public void scanRepos() {
    	try {
        	List<Followed> followed = githubService.getAll();
        	followed.forEach(this::scanFollowed);
    	}
    	catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	/* 
    	 * API call to https://api.github.com/rate_limit , read rate limit
    	 * API call to https://api.github.com/repos/Tejpbit/CodeIT/events?page=1
    	 * API call to https://api.github.com/users/Zomis/received_events/public -- ping the chat user
    	 * API call to https://api.github.com/repos/Tejpbit/CodeIT/commits?page=1&since=YYYY-MM-DDTHH:MM:SSZ
    	 * 
    	 * */
    }
    
    private void scanFollowed(final Followed follow) {
    	long update = Instant.now().getEpochSecond();
    	AbstractEvent[] data = githubBean.fetchEvents(follow);
    	
    	List<AbstractEvent> events = Arrays.asList(data);
    	System.out.println("Last id: " + follow.getLastEventId());
    	System.out.println("IDS before: " + Arrays.toString(events.stream().mapToLong(ev -> ev.getId()).toArray()));
    	events.sort(Comparator.comparingLong(event -> event.getId()));
    	System.out.println("IDS after : " + Arrays.toString(events.stream().mapToLong(ev -> ev.getId()).toArray()));

    	WebhookParameters params = new WebhookParameters();
    	params.setPost(true);
    	params.setRoomId(follow.getRoomIds());

    	Stream<AbstractEvent> stream = events.stream();
    	if (follow.getFollowType() == 1) {
        	stream = stream.filter(ev -> ev instanceof CreateEvent).filter(ev -> ((CreateEvent) ev).getRefType().equals("repository"));
    	}
		stream.forEach(ev -> post(ev, follow.getLastEventId(), params));
    	
    	long eventId = events.stream().mapToLong(ev -> ev.getId()).max().orElse(follow.getLastEventId());
    	System.out.println("Update : " + eventId);
    	githubService.update(follow.getName(), update, eventId, follow.getFollowType() == 1);
	}
    
    private void post(AbstractEvent event, long lastEventId, WebhookParameters params) {
	    if (event.getId() > lastEventId) {
			System.out.println("POST: " + event);
    		controller.post(params, event);
		}
		else {
    		System.out.println(event);
		}
    }

	@Scheduled(cron = "0 0 1 * * *") // second minute hour day day day
	public void dailyMessage() {
		logger.info("time!");
		Map<String, RepositoryStats> stats = statistics.getRepoStats();
    	statistics.reset();
    	String rooms = configService.getConfig("dailyRooms", "");
    	
    	List<Entry<String, RepositoryStats>> statsList = new ArrayList<Entry<String, RepositoryStats>>(stats.entrySet());
    	statsList.sort(Comparator.comparing(ee -> ee.getKey()));
    	
		for (String room : rooms.split(",")) {
   			WebhookParameters params = new WebhookParameters();
   			params.setRoomId(room);
   			params.setPost(true);
			
   			chatBot.postMessages(params, "***RELOAD!***");
    			
   			for (Entry<String, RepositoryStats> statsEntry : statsList) {
   				RepositoryStats stat = statsEntry.getValue();
				String repoMessage = MessageFormat.format("\\[[**{0}**]({1})\\] {2} " + pluralize("commit", stat.getCommits()) 
						+ ". {3} " + pluralize("issue", stat.getIssuesOpened()) + " opened and {4} closed",
					stat.getName(), stat.getUrl(),
					stat.getCommits(), stat.getIssuesOpened(), stat.getIssuesClosed());
				
       			chatBot.postMessages(params, repoMessage);
   			}
    	}
	}

	private String pluralize(String string, int number) {
		return number == 1 ? string : string + 's';
	}
}