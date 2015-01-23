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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;
import com.skiwi.githubhooksechatservice.events.github.CreateEvent;
import com.skiwi.githubhooksechatservice.model.FollowedRepository;
import com.skiwi.githubhooksechatservice.model.FollowedUser;
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
    		updateRepos();
    		updateUsers();
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
    
    private void updateRepos() {
    	List<FollowedRepository> repos = githubService.getAll();
    	for (FollowedRepository repo : repos) {
        	long update = Instant.now().getEpochSecond();
        	AbstractEvent[] data = githubBean.fetchRepoEvents(repo.getName());
        	
        	List<AbstractEvent> events = Arrays.asList(data);
        	System.out.println("Last id: " + repo.getLastEventId());
        	System.out.println("IDS before: " + Arrays.toString(events.stream().mapToLong(ev -> ev.getId()).toArray()));
        	events.sort(Comparator.comparingLong(event -> event.getId()));
        	System.out.println("IDS after : " + Arrays.toString(events.stream().mapToLong(ev -> ev.getId()).toArray()));

        	WebhookParameters params = new WebhookParameters();
        	params.setPost(true);
        	params.setRoomId(repo.getRoomIds());

        	for (AbstractEvent event : events) {
        		if (event.getId() > repo.getLastEventId()) {
        			System.out.println("POST: " + event);
            		controller.post(params, event);
        		}
        		else {
            		System.out.println(event);
        		}
        	}

        	long eventId = events.stream().mapToLong(ev -> ev.getId()).max().orElse(repo.getLastEventId());
        	System.out.println("Update : " + eventId);
        	githubService.update(repo.getName(), update, eventId);
    	}
	}

    private void updateUsers() {
    	List<FollowedUser> users = githubService.getAllUsers();
    	for (FollowedUser user : users) {
        	long update = Instant.now().getEpochSecond();
        	AbstractEvent[] data = githubBean.fetchUserEvents(user.getName());
        	
        	List<AbstractEvent> events = Arrays.asList(data);
        	System.out.println("Last id: " + user.getLastEventId());
        	System.out.println("IDS before: " + Arrays.toString(events.stream().mapToLong(ev -> ev.getId()).toArray()));
        	events.sort(Comparator.comparingLong(event -> event.getId()));
        	System.out.println("IDS after : " + Arrays.toString(events.stream().mapToLong(ev -> ev.getId()).toArray()));

        	WebhookParameters params = new WebhookParameters();
        	params.setPost(false);
        	params.setRoomId(user.getRoomIds());

        	for (AbstractEvent event : events) {
        		if (event instanceof CreateEvent) {
        			CreateEvent ev = (CreateEvent) event;
        			if (ev.getRefType().equals("repository")) {
                		if (event.getId() > user.getLastEventId()) {
                			System.out.println("POST: " + event);
                    		controller.post(params, event);
                		}
                		else {
                    		System.out.println(event);
                		}
        			}
        		}
        	}

        	long eventId = events.stream().mapToLong(ev -> ev.getId()).max().orElse(user.getLastEventId());
        	System.out.println("Update : " + eventId);
        	githubService.updateUser(user.getName(), update, eventId);
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