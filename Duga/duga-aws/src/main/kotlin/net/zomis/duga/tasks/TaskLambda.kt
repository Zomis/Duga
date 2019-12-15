package net.zomis.duga.tasks

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper

class TaskLambda : RequestHandler<Map<String, Any>, Map<String, Any>> {

    private val mapper = ObjectMapper()

    override fun handleRequest(input: Map<String, Any>?, context: Context?): Map<String, Any> {
        val json = mapper.readTree(mapper.writeValueAsString(input))

        // comments
        val task: DugaTask? = when (json["type"].asText()) {
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

        task?.perform()

        return mapOf()
    }

}