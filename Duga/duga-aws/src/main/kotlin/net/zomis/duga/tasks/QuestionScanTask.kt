package net.zomis.duga.tasks

import com.fasterxml.jackson.databind.JsonNode
import net.zomis.duga.aws.DugaMessage
import net.zomis.duga.utils.StackExchangeAPI
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory
import java.time.Instant

class QuestionScanTask(val room: String, val site: String) : DugaTask {
    private val FILTER = "!DEQ-Ts0KBm6n14zYUs8UZUsw.yj0rZkhsEKF2rI4kBp*yOHv4z4"
    private val LATEST_QUESTIONS = "questions?order=desc&sort=activity"
    private val logger = LoggerFactory.getLogger(javaClass)
    private val stackAPI = StackExchangeAPI()

    override fun perform(): List<DugaMessage> {
        val activeQuestions = stackAPI.apiCall(LATEST_QUESTIONS, site, FILTER)
        val lastCheck = Instant.now().minusSeconds(60 * 5)
        return this.perform(activeQuestions, lastCheck).map {
            DugaMessage(room, it)
        }
    }

    private fun perform(result: JsonNode, lastCheck: Instant): List<String> {
        val results = mutableListOf<String>()
        val questions = result["items"]
        questions.forEach { node ->
            val edited = node["last_edit_date"].asLong()
            val questionLink = node["link"].asText()
            val op = formatDisplayName(node["owner"]["display_name"].asText())
            val questionId = node["question_id"].asText()
            if (edited >= lastCheck.epochSecond && node["answer_count"].asInt() > 0) {
                logger.info("$questionId has been edited")
                val edits = stackAPI.apiCall(editCall(questionId), "codereview", "!9YdnS7lAD")
                logger.info("Edits fetched for $questionId: ${edits["items"].size()}. quota remaining ${edits["quota_remaining"]}")
                val possibleInvalidations = codeChanges(edits, lastCheck)
                if (!possibleInvalidations.isEmpty()) {
                    val link = questionLink.replace("/questions/.*", "/posts/$questionId/revisions")
                    val editor = possibleInvalidations.joinToString(", ") {invalidation ->
                        formatDisplayName(invalidation["user"]["display_name"].asText())
                    }

                    results.add("*possible answer invalidation by $editor on question by $op:* $link")
                }
            }
        }
        return results
    }

    private fun formatDisplayName(displayName: String): String {
        return StringEscapeUtils.unescapeHtml4(displayName)
    }

    private fun codeChanges(edits: JsonNode, lastCheck: Instant): List<JsonNode> {
        val result = mutableListOf<JsonNode>()
        edits["items"].forEach {
            if (!it.has("last_body")) {
                return@forEach
            }
            if (it["creation_date"].asLong() < lastCheck.epochSecond) {
                return@forEach
            }
            if (it["is_rollback"].asBoolean()) {
                return@forEach
            }
            val code = stripNonCode(it["body"].asText())
            val codeBefore = stripNonCode(it["last_body"].asText())
            if (code != codeBefore) {
                result.add(it)
            }
        }
        return result
    }

    private fun editCall(id: String): String {
        return "posts/$id/revisions"
    }

    private fun stripNonCode(originalPost: String): String {
        val codeStart = "<code>"
        val codeEnd = "</code>"

        var post = originalPost
        post = post.replace("[\\t ]", "")
        var keepCount = 0
        var index = post.indexOf(codeStart)
        while (index >= 0) {
            val endIndex = post.indexOf(codeEnd)
            if (endIndex < 0) {
                throw IllegalStateException()
            }
            val before = post.substring(0, keepCount)
            val code = post.substring(index + codeStart.length, endIndex)
            val after = post.substring(endIndex + codeEnd.length)
            if (code.contains("\\n") || code.contains('\n')) {
                post = before + code + after
                keepCount += code.length
            } else {
                post = before + after
            }
            index = post.indexOf(codeStart)
        }
        return post.substring(0, keepCount)
    }


}
