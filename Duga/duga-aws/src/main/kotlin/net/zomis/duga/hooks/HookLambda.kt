package net.zomis.duga.hooks

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import net.zomis.duga.aws.Duga
import net.zomis.duga.aws.DugaMessage

fun Map<String, Any>.path(path: String): Any? {
    val paths = path.split('.')
    var current: Any? = this
    for (next in paths) {
        if (current == null) {
            return null
        }
        current = (current as Map<String, Any>)[next]
    }
    return current
}

class HookLambda : RequestHandler<Map<String, Any>, Map<String, Any>> {
    private val mapper = ObjectMapper()

    override fun handleRequest(input: Map<String, Any>?, context: Context?): Map<String, Any> {
        println(input!!)
        val type = input["path"] as String? ?: return response(400, "error" to "No type specified")
        val body = mapper.readTree(input["body"] as String)
        if (input["queryStringParameters"] == null) {
            return response(400, "error" to "You must specify the roomId at the end of the URL using for example `?roomId=20298`")
        }
        val queryParameters = input["queryStringParameters"] as Map<*, *>
        if (!queryParameters.containsKey("roomId")) {
            return response(400, "error" to "You must specify the roomId at the end of the URL using for example `?roomId=20298`")
        }
        val roomId = queryParameters["roomId"] as String

        val hook: DugaWebhook? = when (type) {
            "/splunk" -> SplunkHook()
            "/github" -> GitHubHook(input.path("headers.X-GitHub-Event") as String)
//            "/appveyor" -> AppVeyorHook()
//            "/bitbucket" -> BitBucketHook()
//            "/sonarqube" -> SonarQubeHook()
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