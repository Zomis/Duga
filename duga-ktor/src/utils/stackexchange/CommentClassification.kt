package net.zomis.duga.utils.stackexchange

import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

object CommentClassification {

	private val logger = LoggerFactory.getLogger(CommentClassification::class.java)

    val ML_THRESHOLD = 0.3f
    val REAL = 0.49f
	val DEBUG = 0f
	
	val PROG_LINK = Pattern.compile(
		Pattern.quote("<a href=\"http")
				+ "s?" +
			Pattern.quote("://softwareengineering.stackexchange.com") + "(/|/help/.*)?" + Pattern.quote("\">")
	)

	fun bodyContainsProgrammersLink(body: String): Boolean {
		return PROG_LINK.matcher(body).find();
	}
	
	fun calcInterestingLevelProgrammers(comment: JsonNode): Float {
		val matchPattern = if (bodyContainsProgrammersLink(comment.get("body").asText())) 1.0f else 0f
		return matchPattern + calcInterestingLevelProgrammers(comment.get("body_markdown").asText())
	}
	
	fun calcInterestingLevelSoftwareRecs(comment: JsonNode) = calcInterestingLevelSoftwareRecs(comment.get("body_markdown").asText())
	
	fun calcInterestingLevelSoftwareRecs(comment: String): Float {
		var points = 0.4f;
		points += score(0.3f, comment, "software recommendations")
		points += score(0.3f, comment, "softwarerecs")
//		points -= score(0.25f, comment, "meta.softwarerecs.stackexchange.com/questions/336/");
//		points -= score(0.25f, comment, "meta.softwarerecs.stackexchange.com/q/336/");
		points -= score(0.55f, comment, "/336")
		return points;
	}
	
	fun calcInterestingLevelProgrammers(commentOriginal: String): Float {
		val comment = commentOriginal.toLowerCase()
		if (!comment.contains("programmers")) {
			return 0f
		}
		if (programmersIgnore(comment)) {
			return 0.42f;
		}
		var points = 0.4f;
		
		points += score(0.3f, comment, "better fit");
		points += score(0.3f, comment, "better suited");
		points += score(0.3f, comment, "better place");
		
		points += score(0.01f, comment, "close");
		points += score(0.05f, comment, "off-topic");
		points += score(0.05f, comment, "design");
		points += score(0.05f, comment, "whiteboard");
		points += score(0.05f, comment, "this question");
		points += score(0.15f, comment, "this site");
		points += score(0.2f, comment, "programmers.se");
		points += score(0.07f, comment, "help at");
		points += score(0.07f, comment, "place to ask");
		points += score(0.15f, comment, "migrate");
		points += score(0.1f, comment, "belong");
		points += score(0.02f, comment, "instead");
		points += score(0.03f, comment, "the place for");
		
		points += score(0.03f, comment, "try programmers");
		points += score(0.03f, comment, "for programmers");
		points += score(0.03f, comment, "on programmers");
		points += score(0.03f, comment, "at programmers");
		points += score(0.03f, comment, "to programmers");
		
		return points;
	}

	private val programmersIgnore = arrayOf("please stop using programmers.se as your toilet bowl",
		"/7265", // http://meta.programmers.stackexchange.com/questions/7265/when-is-a-software-licensing-question-on-topic
		"/7182" // http://meta.programmers.stackexchange.com/questions/7182/what-goes-on-programmers-se-a-guide-for-stack-overflow
	)
	
	private fun programmersIgnore(comment: String) = programmersIgnore.any { comment.contains(it) }

	private fun score(f: Float, comment: String, string: String): Float {
		if (comment.contains(string)) {
			logger.info("$string --- $comment --- $f")
			return f
		}
		return 0f
	}

}
