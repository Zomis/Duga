package net.zomis.duga.server.webhooks

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import net.zomis.duga.chat.DugaPoster
import org.slf4j.LoggerFactory

object SplunkWebhook {

    private val logger = LoggerFactory.getLogger(SplunkWebhook::class.java)

    suspend fun post(poster: DugaPoster, room: String, node: JsonNode) {
        try {
            logger.info("Splunk webhook $room: $node")
            val message = node["result"]?.get("message")?.asText() ?: "Splunk Alert: " + node["search_name"].asText()
            poster.postMessage(room, message)
        } catch (e: Exception) {
            logger.warn("Unable to post Splunk webhook $room: $node", e)
        }
        // ${json.search_name} - ${json.result}
    }

    fun route(routing: Routing, poster: DugaPoster) {
        routing.route("/splunk") {
            post {
                // read headers, read params, read body
                post(poster, call.parameters["room"]!!, call.receive())
            }
            post("/splunk/{room}") {
                post(poster, call.parameters["room"]!!, call.receive())
            }
        }
    }

}
