package net.zomis.duga.utils.stackexchange

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import java.time.Instant;

import net.zomis.duga.chat.DugaPoster
import net.zomis.machlearn.text.TextClassification
import org.slf4j.LoggerFactory

class CommentsScanTask(
	private val scope: CoroutineScope,
	private val stackAPI: StackExchangeApi,
	private val programmersClassification: TextClassification,
	poster: DugaPoster
) {
    private val logger = LoggerFactory.getLogger(CommentsScanTask::class.java)
    
    private var nextFetch = Instant.now()
    private var lastComment: Long = 0
    private var fromDate: Long = 0
    private var remainingQuota: Long = 0
    
	private val codeReview = poster.room("8595")
	private val debug = poster.room("20298")
	private val programmers = poster.room("21")
	private val softwareRecs = poster.room("20298")

	suspend fun run() {
		val now = Instant.now()
    	if (!now.isAfter(nextFetch)) {
    		logger.info("Comments scan next fetch is at $nextFetch and now is $now, need to wait a bit more, returning.")
    		return;
    	}

		val comments = stackAPI.fetchComments("stackoverflow", fromDate)
		if (comments == null) {
			logger.error("Unable to get comments from $fromDate")
			return
		}
		val currentQuota = comments.get("quota_remaining").asLong()
		if (currentQuota > remainingQuota && fromDate != 0L) {
			debug.post(Instant.now().toString() + " Quota has been reset. Was " +
				remainingQuota + " is now " + currentQuota)
		}
		remainingQuota = currentQuota

		if (comments.get("backoff") != null) {
			nextFetch = Instant.now().plusSeconds(comments.get("backoff").asLong() + 10)
			debug.post(Instant.now().toString() +
					" Next fetch: " + nextFetch + " because of backoff " + comments.get("backoff"))
			return
		}

		val items = comments.get("items") ?: return
		if (items.size() >= 100) {
			debug.post(Instant.now().toString() + " Warning: Retrieved 100 comments. Might have missed some.")
		}

		val previousLastComment = lastComment
		for (comment in items.reversed()) {
			if (comment.get("comment_id").asLong() <= previousLastComment) {
				continue;
			}
			lastComment = Math.max(comment.get("comment_id").asLong(), lastComment)
			fromDate = Math.max(comment.get("creation_date").asLong(), fromDate)
			if (isInterestingCommentCR(comment)) {
				logComment(comment, "Code Review")
				codeReview.post(comment.get("link").asText())
			}

			classifyProgrammers(comment)

			val softwareCertainty = CommentClassification.calcInterestingLevelSoftwareRecs(comment)

			if (softwareCertainty >= CommentClassification.REAL) {
				softwareRecs.post(comment.get("link").asText())
			}
		}
    }

	private fun logComment(comment: JsonNode, site: String) {
		logger.info("$site comment $comment.comment_id " +
				"on $comment.post_type $comment.post_id " +
				"posted by $comment.owner.display_name " +
				"with $comment.owner.reputation reputation: ${comment.get("body_markdown")}")
	}

	private fun isInterestingCommentCR(comment: JsonNode): Boolean {
		val commentText = comment.get("body_markdown").asText().toLowerCase()
		return commentText.contains("code review") || commentText.contains("codereview")
	}

	private fun classifyProgrammers(comment: JsonNode) {
        val oldClassification = CommentClassification.calcInterestingLevelProgrammers(comment);
        val programmersMLscore = programmersMLscore(comment)

        if (programmersMLscore >= CommentClassification.ML_THRESHOLD) {
            programmers.postAsync(scope, comment.get("link").asText())
        }

        if (programmersMLscore >= CommentClassification.DEBUG) {
			logComment(comment, "Software Engineering (ML $programmersMLscore old $oldClassification)")
            val certaintyLevelMessage =
                    "ML Classification " + programmersMLscore +
                            " (Old classification " + oldClassification + ")";
			scope.launch {
				debug.post(certaintyLevelMessage)
				debug.post(comment.get("link").asText())
			}
        }
    }

    private fun programmersMLscore(comment: JsonNode): Double {
        val text = comment.get("body_markdown").asText().toLowerCase()
        if (!text.contains("programmers") && !text.contains("softwareeng")
	     && !text.contains("software eng")) {
            // No need to check with the Machine Learning system in this case
            return -1.0
        }
        return programmersClassification.score(text)
    }
}
