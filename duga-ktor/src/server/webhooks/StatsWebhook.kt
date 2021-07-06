package net.zomis.duga.server.webhooks

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import net.zomis.duga.utils.stats.DugaStats
import org.slf4j.LoggerFactory

object StatsWebhook {

    class Config {
        data class ItemConfig(val authToken: String, val application: String, val url: String)
        val items: List<ItemConfig> = emptyList()
    }

    private val logger = LoggerFactory.getLogger(StatsWebhook::class.java)

    fun route(routing: Routing, stats: DugaStats, config: Config) {
        routing.route("/stats") {
            get {
                call.respond(HttpStatusCode.OK, stats.currentStats().associate {
                    it.displayName to mapOf(
                        "name" to it.displayName,
                        "url" to it.url,
                        "values" to it.current()
                    )
                })
            }
            post {
                val currentStats = saveStats(stats, call.receive(), config)
                if (currentStats) {
                    call.respond(HttpStatusCode.OK, "Stats is $currentStats")
                } else {
                    call.respond(HttpStatusCode.Forbidden, "Invalid request")
                }
            }
        }
    }

    private fun saveStats(stats: DugaStats, node: JsonNode, config: Config): Boolean {
        logger.info("Incoming: {}", node)
        val authToken = node["authToken"].asText()
        val application = node["application"].asText()
        val statsNode = node["stats"] as ObjectNode
        val itemConfig = config.items.find { it.authToken == authToken && it.application == application }
        if (itemConfig == null) {
            logger.warn("No stats config found for authToken '$authToken' and application '$application'")
            return false
        }

        val secretKey = "$authToken/$application"
        val statsMap = statsNode.fields().asSequence().associate { it.key to it.value.asInt() }
        statsMap.forEach {
            stats.addKey(secretKey, application, itemConfig.url, it.key, it.value)
        }
        return true
    }

}
