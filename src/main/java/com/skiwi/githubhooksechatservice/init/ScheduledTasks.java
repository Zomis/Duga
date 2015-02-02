package com.skiwi.githubhooksechatservice.init;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import com.skiwi.githubhooksechatservice.mvc.beans.StackExchangeAPIBean;
import com.skiwi.githubhooksechatservice.mvc.controllers.GithubHookController;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;
import com.skiwi.githubhooksechatservice.service.ConfigService;
import com.skiwi.githubhooksechatservice.service.DailyService;
import com.skiwi.githubhooksechatservice.service.GithubService;

@Configuration
@EnableScheduling
public class ScheduledTasks {
    private static final Logger logger = LogManager.getLogger(ScheduledTasks.class);
	
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
    private StackExchangeAPIBean stackAPI;
    
    @Autowired
    private GithubHookController controller;

    @Scheduled(cron = "0 0 */2 * * *") // second minute hour day day day
    public void scanRepos() {
    	final int API_LIMIT = 4; // 12 times per hour, 4 items each time = 48 requests. API limit is 60. Leaves some space for other uses.
    	try {
        	List<Followed> followed = new ArrayList<Followed>(githubService.getAll());
        	followed.sort(Comparator.comparingLong(follow -> follow.getLastChecked()));
        	followed.stream()
        		.limit(API_LIMIT)
        		.forEach(this::scanFollowed);
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

    private Instant nextFetch = Instant.now();
    private long lastComment;
    private long fromDate;

	private final WebhookParameters params = WebhookParameters.toRoom("8595");
	private final WebhookParameters debug = WebhookParameters.toRoom("20298");
	
    @Scheduled(cron = "0 */2 * * * *") // second minute hour day day day
    public void scanComments() {
    	if (!Instant.now().isAfter(nextFetch)) {
    		return;
    	}

    	try {
    		StackComments comments = stackAPI.fetchComments("stackoverflow", fromDate);
    		List<StackExchangeComment> items = comments.getItems();
    		if (items != null) {
    			if (items.size() >= 100) {
    				chatBot.postMessage(debug, Instant.now() + " Warning: Retrieved 100 comments. Might have missed some.");
    			}
    			
    			long previousLastComment = lastComment;
    			lastComment = items.stream().mapToLong(comment -> comment.getCommentId()).max().orElse(lastComment);
    			fromDate = items.stream().mapToLong(comment -> comment.getCreationDate()).max().orElse(fromDate);
    			for (StackExchangeComment comment : items) {
    				if (comment.getCommentId() <= previousLastComment) {
    					continue;
    				}
    				if (isInterestingComment(comment)) {
    					chatBot.postMessage(params, comment.getLink());
    				}
    			}
    		}
    		if (comments.getBackoff() != 0) {
    			nextFetch = Instant.now().plusSeconds(comments.getBackoff() + 10);
    			chatBot.postMessage(debug, Instant.now() + " Next fetch: " + nextFetch + " because of backoff " + comments.getBackoff());
    		}
    	} catch (Exception e) {
    		logger.error("Error retrieving comments", e);
    		chatBot.postMessage(debug, Instant.now() + " Exception in comment task " + e);
    		return;
    	}
    }
    
    private boolean isInterestingComment(StackExchangeComment comment) {
		String commentText = comment.getBodyMarkdown().toLowerCase();
    	return commentText.contains("code review") || commentText.contains("codereview");
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