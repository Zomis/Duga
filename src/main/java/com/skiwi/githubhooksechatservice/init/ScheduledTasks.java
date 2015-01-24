package com.skiwi.githubhooksechatservice.init;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;
import com.skiwi.githubhooksechatservice.model.DailyInfo;
import com.skiwi.githubhooksechatservice.model.Followed;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubBean;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubEventFilter;
import com.skiwi.githubhooksechatservice.mvc.controllers.GithubHookController;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;
import com.skiwi.githubhooksechatservice.service.ConfigService;
import com.skiwi.githubhooksechatservice.service.DailyService;
import com.skiwi.githubhooksechatservice.service.GithubService;

@Configuration
@EnableScheduling
public class ScheduledTasks {
    private static final Logger logger = Logger.getLogger(ScheduledTasks.class.getSimpleName());
	
    private final GithubEventFilter eventFilter = new GithubEventFilter();
    
    @Autowired
    private ChatBot chatBot;
    
    @Autowired
    private DailyService dailyService;
    
    @Autowired
    private ConfigService configService;

    @Autowired
    private GithubService githubService;

    @Autowired
    private GithubBean githubBean;
    
    @Autowired
    private GithubHookController controller;

    @Scheduled(cron = "0 */5 * * * *") // second minute hour day day day
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
    	List<AbstractEvent> events;
		try {
			events = githubBean.fetchEvents(follow);
		} catch (IOException e) {
    		return;
    	}

    	WebhookParameters params = new WebhookParameters();
    	params.setPost(true);
    	params.setRoomId(follow.getRoomIds());

    	Stream<AbstractEvent> stream = events.stream();
    	stream = eventFilter.filter(stream, follow.getInterestingEvents());
    	
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
		
		List<DailyInfo> results = new ArrayList<>(dailyService.getAndReset());
    	String rooms = configService.getConfig("dailyRooms", "");
    	results.sort(Comparator.comparing(ee -> ee.getName()));
    	
		for (String room : rooms.split(",")) {
   			WebhookParameters params = new WebhookParameters();
   			params.setRoomId(room);
   			params.setPost(true);
			
   			chatBot.postMessages(params, "***RELOAD!***");
    		
   			for (DailyInfo stat : results) {
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