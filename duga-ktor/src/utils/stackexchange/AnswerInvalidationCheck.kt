package net.zomis.duga.utils.stackexchange

import com.fasterxml.jackson.databind.JsonNode
import net.zomis.duga.chat.DugaPoster
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import java.time.Instant

object AnswerInvalidationCheck {

    private val logger = LoggerFactory.getLogger(AnswerInvalidationCheck::class.java)

    suspend fun perform(poster: DugaPoster, result: JsonNode?, lastCheck: Instant, stackExchangeAPI: StackExchangeApi) {
        if (result == null) {
            logger.error("No questions gathered for Answer Invalidation")
            return
        }
        logger.debug("Answer invalidation check")
        val questions = result.get("items")
        questions.forEach { question ->
//            def created = it.creation_date
//            def activity = it.creation_date
            val edited = question.get("last_edit_date")?.asLong() ?: 0
            val questionLink = question.get("link").asText()
            val op = formatDisplayName(question.get("owner").get("display_name").asText())
            val questionId = question.get("question_id").asLong()
            if (edited >= lastCheck.epochSecond && question.get("answer_count").asLong() > 0) {
                logger.info("edited: $questionId")
                val edits = stackExchangeAPI.apiCall(editCall(questionId), "codereview", "!9YdnS7lAD")
                    ?: throw IllegalStateException("Unable to get edits for $questionId")
                poster.postMessage("20298", "Edits fetched for $questionId: ${edits.get("items").size()}. quota remaining ${edits.get("quota_remaining")}")
                val possibleInvalidations = codeChanges(edits, lastCheck)
                if (possibleInvalidations.isNotEmpty()) {
                    val link = questionLink.replace(Regex("/questions/.*"), "/posts/$questionId/revisions")
                    val editor = possibleInvalidations
                        .map { formatDisplayName(it.get("user").get("display_name").asText()) }
                        .joinToString(", ")
                    poster.postMessage("8595", "*possible answer invalidation by $editor on question by $op:* $link")
                }
            }
        }
    }

    fun formatDisplayName(displayName: String): String {
        return StringEscapeUtils.unescapeHtml4(displayName)
    }

    fun codeChanged(edits: JsonNode, lastCheck: Instant): Boolean {
        return codeChanges(edits, lastCheck).isNotEmpty()
    }

    fun codeChanges(edits: JsonNode, lastCheck: Instant): List<JsonNode> {
        val result = mutableListOf<JsonNode>()
        edits.get("items").forEach {
            val lastBody = it.get("last_body")
            if (lastBody == null || lastBody.isNull) {
                return@forEach
            }
            if (it.get("creation_date").asLong() < lastCheck.epochSecond) {
                return@forEach
            }
            if (it.get("is_rollback").asBoolean()) {
                return@forEach
            }
            val code = stripNonCode(it.get("body").asText())
            val codeBefore = stripNonCode(it.get("last_body").asText())
            if (!code.equals(codeBefore)) {
                result.add(it)
            }
        }
        return result
    }

    fun editCall(id: Long) = "posts/$id/revisions"

    fun stripNonCode(original: String): String {
        var post = original.replace(Regex("[\\t ]"), "")
        var keepCount = 0
        var index = post.indexOf("<code>")
        while (index >= 0) {
            val endIndex = post.indexOf("</code>")
            check(endIndex >= 0)
            val before = post.substring(0, keepCount)
            val code = post.substring(index + "<code>".length, endIndex)
            val after = post.substring(endIndex + "</code>".length)
            if (code.contains("\\n") || code.contains('\n')) {
                post = before + code + after
                keepCount += code.length
            } else {
                post = before + after
            }
            index = post.indexOf("<code>")
        }
        return post.substring(0, keepCount)
    }

}
