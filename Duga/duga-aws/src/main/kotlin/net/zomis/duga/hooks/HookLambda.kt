package net.zomis.duga.hooks

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import net.zomis.duga.aws.Duga
import net.zomis.duga.aws.DugaMessage

class HookLambda : RequestHandler<Map<String, Any>, Map<String, Any>> {
    private val mapper = ObjectMapper()

    override fun handleRequest(input: Map<String, Any>?, context: Context?): Map<String, Any> {
        println(input!!)
        val type = input["path"] as String? ?: return response(400, "error" to "No type specified")
        val body = mapper.readTree(input["body"] as String)
        val roomId = (input["queryStringParameters"] as Map<*, *>)["roomId"] as String? ?: "16134"

        val hook: DugaWebhook? = when (type) {
            "/splunk" -> SplunkHook()
            else -> null
        }
        val messages = hook?.handle(body)?.map { DugaMessage(roomId, it) }
        messages?.forEach { println("Hook message: $it") }
        if (messages != null && messages.isNotEmpty()) {
            Duga().sendMany(messages)
        }

        // TODO: Message can be sent directly, without the need for passing through SQS (need to fix IAM roles though)
        return if (messages.isNullOrEmpty()) response(500) else response(200)
    }

    private fun response(responseCode: Int, vararg pairs: Pair<String, Any>): Map<String, Any> {
        val body: Map<String, Any> = pairs.fold(mapOf()) { r, entry -> r.plus(entry) }
        return mapOf(
            "isBase64Encoded" to false,
            "statusCode" to responseCode,
            "headers" to {},
            "body" to mapper.writeValueAsString(body)
        )
    }
}