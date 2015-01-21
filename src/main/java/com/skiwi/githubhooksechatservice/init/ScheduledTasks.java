package com.skiwi.githubhooksechatservice.init;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Arrays;
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
import com.skiwi.githubhooksechatservice.model.FollowedRepository;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubBean;
import com.skiwi.githubhooksechatservice.mvc.beans.RepositoryStats;
import com.skiwi.githubhooksechatservice.mvc.beans.Statistics;
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

    @Scheduled(cron = "0 0 * * * *") // second minute hour day day day
    public void scanRepos() {
    	List<FollowedRepository> repos = githubService.getAll();
    	
    	for (FollowedRepository repo : repos) {
        	AbstractEvent[] data = githubBean.fetchRepoEvents(repo.getName());
        	long update = Instant.now().getEpochSecond();
        	
        	for (AbstractEvent event : data) {
        		System.out.println(event);
        	}

//        	System.out.println(Arrays.toString(data));
        	long eventId = Arrays.stream(data).mapToLong(ev -> ev.getId()).max().orElse(0);
        	githubService.update(repo.getName(), update, eventId);
    	}
    	
    	/* 
    	 * API call to https://api.github.com/rate_limit , read rate limit
    	 * API call to https://api.github.com/repos/Tejpbit/CodeIT/events?page=1
    	 * API call to https://api.github.com/users/Zomis/received_events/public -- ping the chat user
    	 * API call to https://api.github.com/repos/Tejpbit/CodeIT/commits?page=1&since=YYYY-MM-DDTHH:MM:SSZ
    	 * 
    	 * */
    }
    
    @Scheduled(cron = "0 0 1 * * *") // second minute hour day day day
	public void dailyMessage() {
		logger.info("time!");
		Map<String, RepositoryStats> stats = statistics.getRepoStats();
    	statistics.reset();
    	String rooms = configService.getConfig("dailyRooms", "");
    	
		for (String room : rooms.split(",")) {
   			WebhookParameters params = new WebhookParameters();
   			params.setRoomId(room);
   			params.setPost(true);
			
   			chatBot.postMessages(params, "***RELOAD!***");
    			
   			for (Entry<String, RepositoryStats> statsEntry : stats.entrySet()) {
   				RepositoryStats stat = statsEntry.getValue();
				String repoMessage = MessageFormat.format("\\[[**{0}**]({1})\\] {2} commits. {3} issues opened and {4} closed",
					stat.getName(), stat.getUrl(),
					stat.getCommits(), stat.getIssuesOpened(), stat.getIssuesClosed());
				
       			chatBot.postMessages(params, repoMessage);
   			}
    	}
	}
}