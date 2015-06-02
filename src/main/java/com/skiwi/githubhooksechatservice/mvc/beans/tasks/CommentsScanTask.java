package com.skiwi.githubhooksechatservice.mvc.beans.tasks;

import java.time.Instant;
import java.util.Collections;
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
	private final WebhookParameters softwareRecs = WebhookParameters.toRoom("22668");

	private StackExchangeAPIBean stackAPI;

	private ChatBot chatBot;
	
	private Pattern interestingComment = Pattern.compile("[*`_]{,2}code[*`_]{,2}\s*[*`_]{,2}review[*`_]{,2}", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	
	public CommentsScanTask(StackExchangeAPIBean stackAPI, ChatBot chatBot) {
		this.stackAPI = stackAPI;
		this.chatBot = chatBot;
	}

	private boolean isInterestingComment(StackExchangeComment comment) {
		String commentText = comment.getBodyMarkdown().toLowerCase();
		return interestingComment.matcher(commentText).find(0);
		// We're using `find()` instead of `matches()` because it can occur anywhere and we don't wanna reject if we can't match the whole comment
		// See https://docs.oracle.com/javase/8/docs/api/java/util/regex/Matcher.html#matches-- and #find-int- for more information
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
					chatBot.postMessage(debug, Instant.now() + " Warning: Retrieved 100 comments. Might have missed some.");
				}
				
				long previousLastComment = lastComment;
				Collections.reverse(items);
				for (StackExchangeComment comment : items) {
					if (comment.getCommentId() <= previousLastComment) {
						continue;
					}
					lastComment = Math.max(comment.getCommentId(), lastComment);
					fromDate = Math.max(comment.getCreationDate(), fromDate);
					if (isInterestingComment(comment)) {
						chatBot.postMessage(params, comment.getLink());
					}
					float programmersCertainty = CommentClassification.calcInterestingLevelProgrammers(comment);
					
					if (programmersCertainty >= CommentClassification.REAL) {
						chatBot.postMessage(programmers, comment.getLink());
					}
					if (programmersCertainty >= CommentClassification.DEBUG) {
						chatBot.postMessage(debug, "Certainty level " + programmersCertainty);
						chatBot.postMessage(debug, comment.getLink());
					}
					
					float softwareCertainty = CommentClassification.calcInterestingLevelSoftwareRecs(comment);
					
					if (softwareCertainty >= CommentClassification.REAL) {
						chatBot.postMessage(softwareRecs, comment.getLink());
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
