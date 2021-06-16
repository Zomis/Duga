package net.zomis.duga.server.webhooks

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import net.zomis.duga.utils.stats.DugaStats
import org.slf4j.LoggerFactory

object StatsWebhook {

    private val logger = LoggerFactory.getLogger(StatsWebhook::class.java)

    fun route(routing: Routing, stats: DugaStats) {
        routing.route("/stats") {
            post {
                val currentStats = saveStats(stats, call.receive())
                call.respond(HttpStatusCode.OK, "Stats is $currentStats")
            }
        }
    }

    private fun saveStats(stats: DugaStats, node: JsonNode) {
        logger.info("Incoming: {}", node)
    }

}
