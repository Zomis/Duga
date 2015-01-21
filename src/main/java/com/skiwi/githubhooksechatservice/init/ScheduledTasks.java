package com.skiwi.githubhooksechatservice.init;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.mvc.beans.RepositoryStats;
import com.skiwi.githubhooksechatservice.mvc.beans.Statistics;
import com.skiwi.githubhooksechatservice.mvc.configuration.BotConfiguration;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;
import com.skiwi.githubhooksechatservice.service.ConfigService;

@Configuration
@EnableScheduling
public class ScheduledTasks {
    private static final Logger logger = Logger.getLogger(ScheduledTasks.class.getSimpleName());
	
    @Autowired
    private ChatBot chatBot;
    
    @Autowired
    private BotConfiguration config;
    
    @Autowired
    private Statistics statistics;
    
    @Autowired
    private ConfigService configService;

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