package net.zomis.duga.tasks;

import java.time.Instant;

import net.zomis.duga.DugaBot;
import net.zomis.duga.StackExchangeAPI;
import net.zomis.duga.chat.WebhookParameters;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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

	private StackExchangeAPI stackAPI;

	private DugaBot chatBot;
	
    public CommentsScanTask(StackExchangeAPI stackAPI, DugaBot chatBot) {
		this.stackAPI = stackAPI;
		this.chatBot = chatBot;
	}

	static boolean isInterestingComment(comment) {
		String commentText = comment.body_markdown.toLowerCase();
    	return commentText.contains("code review") || commentText.contains("codereview");
	}

	@Override
    public void run() {
    	if (!Instant.now().isAfter(nextFetch)) {
    		return;
    	}

    	try {
    		def comments = stackAPI.fetchComments("stackoverflow", fromDate);
    		int currentQuota = comments.quota_remaining
    		if (currentQuota > remainingQuota && fromDate != 0) {
				chatBot.postSingle(debug, Instant.now().toString() + " Quota has been reset. Was " + remainingQuota + " is now " + currentQuota);
    		}
    		remainingQuota = currentQuota;
    		List items = comments.items;
    		if (items) {
    			if (items.size() >= 100) {
    				chatBot.postSingle(debug, Instant.now().toString() + " Warning: Retrieved 100 comments. Might have missed some.");
    			}
    			
    			long previousLastComment = lastComment;
        		Collections.reverse(items);
    			for (def comment in items) {
                    scanComment(comment)
    			}
                items.clear();
            }
            if (comments.backoff) {
                nextFetch = Instant.now().plusSeconds((int) comments.backoff + 10);
                chatBot.postSingle(debug, Instant.now().toString() + " Next fetch: " + nextFetch + " because of backoff " + String.valueOf(comments.backoff));
            }
    	} catch (Exception e) {
    		logger.error("Error retrieving comments", e);
    		chatBot.postSingle(debug, Instant.now().toString() + " Exception in comment task " + e);
    	}
    }

    void scanComment(Object comment) {
        if (comment.comment_id <= previousLastComment) {
            return;
        }
        lastComment = Math.max(comment.comment_id as long, lastComment);
        fromDate = Math.max(comment.creation_date as long, fromDate);
        if (isInterestingComment(comment)) {
            chatBot.postSingle(params, comment.link as String);
        }
        float programmersCertainty = CommentClassification.calcInterestingLevelProgrammers(comment);

        if (programmersCertainty >= CommentClassification.REAL) {
            chatBot.postSingle(programmers, comment.link as String);
        }
        if (programmersCertainty >= CommentClassification.DEBUG) {
            chatBot.postSingle(debug, "Certainty level " + programmersCertainty);
            chatBot.postSingle(debug, comment.link as String);
        }

        float softwareCertainty = CommentClassification.calcInterestingLevelSoftwareRecs(comment);

        if (softwareCertainty >= CommentClassification.REAL) {
            chatBot.postSingle(softwareRecs, comment.link as String);
        }
    }
}
