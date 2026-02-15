package net.zomis.duga.server.webhooks

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import net.zomis.duga.chat.DugaPoster
import org.slf4j.LoggerFactory

object AppVeyorWebhook {

    private val logger = LoggerFactory.getLogger(AppVeyorWebhook::class.java)

    fun route(routing: Routing, poster: DugaPoster) {
        routing.route("/appveyor") {
            post {
                post(poster, call.parameters["room"]!!, call.receive())
            }
        }
        routing.route("/appveyor/{room}") {
            post {
                post(poster, call.parameters["room"]!!, call.receive())
            }
        }
    }

    suspend fun post(poster: DugaPoster, room: String, node: JsonNode) {
        logger.info("Incoming $room: $node")
        val eventName = node.get("eventName").asText().replace('_', ' ')
        val event = node.get("eventData")
        val repository = "http://github.com/${event.get("repositoryName").asText()}"

        val message = "\\[[**${event.get("repositoryName").asText()}**]($repository)\\] " +
        "[**build #${event["buildNumber"].asText()}**](${event["buildUrl"].asText()}) for commit " +
                "[**${event["commitId"].asText()}**]($repository/commit/${event.get("commitId").asText()}) " +
                "@ [**${event["buildVersion"].asText()}**]($repository/tree/${event.get("branch").asText()}) " +
                eventName
        poster.postMessage(room, message)
        if (eventName == "build_failure") {
            poster.postMessage(room, "**BUILD FAILURE!**")
        }
    }

}