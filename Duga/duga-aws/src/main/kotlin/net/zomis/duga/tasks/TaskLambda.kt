package net.zomis.duga.tasks

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import net.zomis.duga.aws.Duga

class TaskLambda : RequestHandler<Map<String, Any>, Map<String, Any>> {

    private val mapper = ObjectMapper()

    override fun handleRequest(input: Map<String, Any>?, context: Context?): Map<String, Any> {
        val json = mapper.readTree(mapper.writeValueAsString(input))

        val type = json["type"].asText()
        val task: DugaTask? = when (type) {
            "mess" -> MessageTask(json["room"]!!.asText(), json["message"]!!.asText())
            "questionScan" -> null
            "ratingdiff" -> RatingDiffTask(json["room"]!!.asText(),
                json["site"]!!.asText(),
                json["users"]!!.map { it.asText() }
            )
            "unanswered" -> UnansweredTask(json["room"]!!.asText(),
                json["site"]!!.asText(),
                json["message"]!!.asText()
            )
            else -> null
        }

        if (task == null) {
            return mapOf("error" to "No such task: $type")
        }

        val messages = task.perform()
        if (!messages.isEmpty()) {
            Duga().sendMany(messages)
        }

        return mapOf("messages" to messages)
    }

}