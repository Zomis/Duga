package com.skiwi.githubhooksechatservice.mvc.beans.tasks;

import java.time.Instant;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.init.CommentClassification;
import com.skiwi.githubhooksechatservice.mvc.beans.StackExchangeAPIBean;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;
import com.skiwi.githubhooksechatservice.stackapi.StackComments;
import com.skiwi.githubhooksechatservice.stackapi.StackExchangeComment;

public class CommentsScanTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(CommentsScanTask.class);
    
    private Instant nextFetch = Instant.now();
    private long lastComment;
    private long fromDate;
    private int remainingQuota;
    
	private final WebhookParameters params = WebhookParameters.toRoom("8595");
	private final WebhookParameters debug = WebhookParameters.toRoom("20298");
	private final WebhookParameters programmers = WebhookParameters.toRoom("21");

	private StackExchangeAPIBean stackAPI;

	private ChatBot chatBot;
	
    public CommentsScanTask(StackExchangeAPIBean stackAPI, ChatBot chatBot) {
		this.stackAPI = stackAPI;
		this.chatBot = chatBot;
	}

	private boolean isInterestingComment(StackExchangeComment comment) {
		String commentText = comment.getBodyMarkdown().toLowerCase();
    	return commentText.contains("code review") || commentText.contains("codereview");
	}

	@Override
    public void run() {
    	if (!Instant.now().isAfter(nextFetch)) {
    		return;
    	}

    	try {
    		StackComments comments = stackAPI.fetchComments("stackoverflow", fromDate);
    		int currentQuota = comments.getQuotaRemaining();
    		if (currentQuota > remainingQuota && fromDate != 0) {
				chatBot.postMessage(debug, Instant.now() + " Quota has been reset. Was " + remainingQuota + " is now " + currentQuota);
    		}
    		remainingQuota = currentQuota;
    		List<StackExchangeComment> items = comments.getItems();
    		if (items != null) {
    			if (items.size() >= 100) {
    				chatBot.postMessage(debug, Instant.now() + " Warning: Retrieved 100 comments. Might have missed some. This is unlikely to happen");
    			}
    			
    			long previousLastComment = lastComment;
    			for (StackExchangeComment comment : items) {
    				if (comment.getCommentId() <= previousLastComment) {
    					continue;
    				}
    				lastComment = Math.max(comment.getCommentId(), lastComment);
    				fromDate = Math.max(comment.getCreationDate(), fromDate);
    				if (isInterestingComment(comment)) {
    					chatBot.postMessage(params, comment.getLink());
    				}
    				float programmersCertainty = CommentClassification.calcInterestingLevelProgrammers(comment.getBodyMarkdown());
    				
    				if (programmersCertainty >= CommentClassification.REAL) {
    					chatBot.postMessage(programmers, comment.getLink());
    				}
    				if (programmersCertainty >= CommentClassification.DEBUG) {
    					chatBot.postMessage(debug, "Certainty level " + programmersCertainty);
    					chatBot.postMessage(debug, comment.getLink());
    				}
    			}
                items.clear();
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

}
