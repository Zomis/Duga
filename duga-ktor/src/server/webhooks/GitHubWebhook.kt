package net.zomis.duga.server.webhooks

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import org.slf4j.LoggerFactory

class GitHubWebhook(
    private val poster: DugaPoster,
    private val gitHubApi: GitHubApi,
    private val hookString: HookString
) {

    private val logger = LoggerFactory.getLogger(GitHubWebhook::class.java)

    suspend fun post(call: ApplicationCall, room: String?, gitHubEvent: String, jsonNode: JsonNode) {
        if (room == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing room")
            return
        }
        val result = hookString.postGithub(gitHubEvent, jsonNode)
        GlobalScope.launch {
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

    fun route(routing: Routing) {
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
            post(call, room, gitHubEvent, node)
        }
    }

}