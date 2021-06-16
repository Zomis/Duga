package net.zomis.duga.server.webhooks

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import net.zomis.duga.chat.DugaPoster

object SplunkWebhook {

    suspend fun post(poster: DugaPoster, room: String, node: JsonNode) {
        poster.postMessage(room, node.toString())
        // ${json.search_name} - ${json.result}
    }

    fun route(routing: Routing, poster: DugaPoster) {
        routing.route("/splunk") {
            post {
                // read headers, read params, read body
                post(poster, call.parameters["room"]!!, call.receive())
            }
            post("{room}") {

            }
        }
    }

}
