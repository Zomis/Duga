package com.skiwi.githubhooksechatservice.mvc.beans.tasks;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.model.DailyInfo;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;
import com.skiwi.githubhooksechatservice.service.ConfigService;
import com.skiwi.githubhooksechatservice.service.DailyService;

public class StatisticTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(StatisticTask.class);
	private final DailyService dailyService;
	private final ConfigService configService;
	private final ChatBot chatBot;

    public StatisticTask(DailyService dailyService, ConfigService configService, ChatBot chatBot) {
		this.dailyService = dailyService;
		this.configService = configService;
		this.chatBot = chatBot;
	}
	
    @Override
	public void run() {
		logger.info("time!");
		
		List<DailyInfo> results = new ArrayList<>(dailyService.getAndReset());
    	String rooms = configService.getConfig("dailyRooms", "");
    	results.sort(Comparator.comparing(ee -> ee.getName().toLowerCase()));
    	
		for (String room : rooms.split(",")) {
   			WebhookParameters params = new WebhookParameters();
   			params.setRoomId(room);
   			params.setPost(true);
			
   			chatBot.postMessages(params, "***RELOAD!***");
    		
   			for (DailyInfo stat : results) {
   				StringBuilder str = new StringBuilder(MessageFormat.format("\\[[**{0}**]({1})\\]",
					stat.getName(), stat.getUrl()));
   				int startLength = str.length();
   				
   				addStat(str, stat.getCommits(), "commit");
   				addStat(str, stat.getIssuesOpened(), "opened issue");
   				addStat(str, stat.getIssuesClosed(), "closed issue");
   				
   				addStat(str, stat.getComments(), "issue comment");
   				addStat(str, stat.getAdditions(), "addition");
   				addStat(str, stat.getDeletions(), "deletion");
   				if (str.length() > startLength) {
   	       			chatBot.postMessages(params, str.toString());
   				}
   			}
    	}
	}

	private void addStat(StringBuilder str, int count, String text) {
		if (count > 0) {
			str.append(' ');
			str.append(count);
			str.append(' ');
			str.append(text);
			if (count > 1) {
				str.append('s');
			}
			str.append('.');
		}
	}

}
