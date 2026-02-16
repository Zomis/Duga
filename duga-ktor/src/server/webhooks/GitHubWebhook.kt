package net.zomis.duga.server.webhooks

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.utils.github.HookString
import org.slf4j.LoggerFactory
import java.io.OutputStream

class GitHubWebhook(
    private val poster: DugaPoster,
    private val hookString: HookString
) {

    private val logger = LoggerFactory.getLogger(GitHubWebhook::class.java)

    suspend fun post(
        call: ApplicationCall, coroutineScope: CoroutineScope,
        room: String?, gitHubEvent: String, jsonNode: JsonNode
    ) {
        if (room == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing room")
            return
        }
        val result = hookString.postGithub(gitHubEvent, jsonNode)
        coroutineScope.launch {
            result.forEach {
                poster.postMessage(room, it)
            }
        }
        if (result.isNotEmpty()) {
            call.respond("OK")
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }

    suspend fun lambdaPost(
        mapper: ObjectMapper,
        output: OutputStream,
        room: String?, gitHubEvent: String, jsonNode: JsonNode
    ) {
        fun writeResponse(statusCode: Int, payload: Any) {
            println("Response $statusCode: $payload")
            val response = mapOf(
                "statusCode" to statusCode,
                "headers" to mapOf("Content-Type" to "application/json"),
                "body" to mapper.writeValueAsString(payload)
            )
            mapper.writeValue(output, response)
        }
        if (room == null) {
            writeResponse(HttpStatusCode.BadRequest.value, "Missing room")
            return
        }
        val result = hookString.postGithub(gitHubEvent, jsonNode)
        println("Received result of size ${result.size}")
        result.forEach {
            poster.postMessage(room, it)
        }
        if (result.isNotEmpty()) {
            writeResponse(200, "OK")
        } else {
            writeResponse(HttpStatusCode.NoContent.value, "")
        }
    }

    fun route(routing: Routing, coroutineScope: CoroutineScope) {
        routing.post("/github") {
            val room = this.call.parameters["room"]
            val node = this.call.receive<JsonNode>()
            val gitHubEvent = call.request.header("X-GitHub-Event")
            logger.warn("Incoming GitHub without room in URL: $gitHubEvent $room $node")
        }
        routing.post("/github/{room}") {
            val room = this.call.parameters["room"]
            val node = this.call.receive<JsonNode>()
            val gitHubEvent = call.request.header("X-GitHub-Event")
            logger.info("Incoming $gitHubEvent $room $node")
            if (gitHubEvent == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing 'X-GitHub-Event' header")
                return@post
            }
            post(call, coroutineScope, room, gitHubEvent, node)
        }
    }

}