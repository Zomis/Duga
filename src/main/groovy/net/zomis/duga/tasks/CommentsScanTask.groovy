package net.zomis.duga.tasks

import net.zomis.machlearn.text.TextClassification;

import java.time.Instant;

import net.zomis.duga.DugaBotService;
import net.zomis.duga.StackExchangeAPI;
import net.zomis.duga.chat.BotRoom;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger

public class CommentsScanTask implements Runnable {
    private static final Logger logger = LogManager.getLogger(CommentsScanTask.class);
    
    private Instant nextFetch = Instant.now();
    private long lastComment;
    private long fromDate;
    private int remainingQuota;
    
	private final BotRoom codeReview;
	private final BotRoom debug;
	private final BotRoom programmers;
	private final BotRoom softwareRecs;

	private StackExchangeAPI stackAPI;

	private DugaBotService chatBot;
	private final TextClassification programmersClassification;

    public CommentsScanTask(StackExchangeAPI stackAPI, DugaBotService chatBot) {
		this.stackAPI = stackAPI;
		this.chatBot = chatBot;
		this.codeReview = chatBot.room("8595");
		this.debug = chatBot.room("20298");
		this.programmers = chatBot.room("21");
		this.softwareRecs = chatBot.room("22668");

		URL trainingData = getClass().getClassLoader()
				.getResource("trainingset-programmers-comments.txt");
        String source = trainingData?.text;
        String[] lines = source?.split("\n");
        this.programmersClassification = ProgrammersClassification.machineLearning(lines);
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
				chatBot.postAsync(debug.message(Instant.now().toString() + " Quota has been reset. Was " +
					remainingQuota + " is now " + currentQuota));
    		}
    		remainingQuota = currentQuota;
    		List items = comments.items;
    		if (items) {
    			if (items.size() >= 100) {
    				chatBot.postAsync(debug.message(Instant.now().toString() +
                        " Warning: Retrieved 100 comments. Might have missed some."));
    			}
    			
    			long previousLastComment = lastComment;
        		Collections.reverse(items);
    			for (def comment in items) {
    				if (comment.comment_id <= previousLastComment) {
    					continue;
    				}
    				lastComment = Math.max(comment.comment_id as long, lastComment);
    				fromDate = Math.max(comment.creation_date as long, fromDate);
    				if (isInterestingComment(comment)) {
    					chatBot.postAsync(codeReview.message(comment.link as String));
    				}

                    classifyProgrammers(comment);

    				float softwareCertainty = CommentClassification.calcInterestingLevelSoftwareRecs(comment);
    				
    				if (softwareCertainty >= CommentClassification.REAL) {
    					chatBot.postAsync(softwareRecs.message(comment.link as String));
    				}
    			}
                items.clear();
            }
            if (comments.backoff) {
                nextFetch = Instant.now().plusSeconds((int) comments.backoff + 10);
                chatBot.postAsync(debug.message(Instant.now().toString() +
                    " Next fetch: " + nextFetch + " because of backoff " + String.valueOf(comments.backoff)));
            }
    	} catch (Exception e) {
    		logger.error("Error retrieving comments", e);
    		chatBot.postAsync(debug.message(Instant.now().toString() + " Exception in comment task " + e));
    	}
    }

    void classifyProgrammers(def comment) {
        float oldClassification = CommentClassification.calcInterestingLevelProgrammers(comment);
        double programmersMLscore = programmersMLscore(comment)

        if (programmersMLscore >= CommentClassification.ML_THRESHOLD) {
            chatBot.postAsync(programmers.message(comment.link as String));
        }

        if (programmersMLscore >= CommentClassification.DEBUG) {
            String certaintyLevelMessage =
                    "ML Classification " + programmersMLscore +
                            " (Old classification " + oldClassification + ")";
            chatBot.postChat(debug.messages(
                    certaintyLevelMessage, comment.link as String));
        }
    }

    double programmersMLscore(def comment) {
        String text = comment.body_markdown
        if (!text.toLowerCase().contains("programmers")) {
            // No need to check with the Machine Learning system in this case
            return -1;
        }
        return programmersClassification.score(text);
    }
}
