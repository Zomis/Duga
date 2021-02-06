package net.zomis.duga

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.runBlocking
import net.zomis.duga.chat.*
import net.zomis.duga.server.DugaServer
import net.zomis.duga.tasks.Tasks
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import net.zomis.duga.utils.stats.DugaStatsNoOp
import org.slf4j.LoggerFactory
import java.io.File
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

object DugaMain {
    private val logger = LoggerFactory.getLogger(DugaMain::class.java)

    fun start() {
        // Dependencies and basic setup
        val client = DugaClient(jacksonObjectMapper().readValue(File("bot.secret"), BotConfig::class.java))
        val bot = DugaBot(client.client, client.config) { httpClient, botConfig ->
            val se = StackExchangeLogin(httpClient, botConfig)
            if (se.login()) {
                se.fkeyReal()
            } else throw RuntimeException()
        }
        val poster = DugaPoster(bot)
        DugaServer(poster).start()

        val gitHubApi = GitHubApi(client.client, readSecret("github"))
        val hookString = HookString(DugaStatsNoOp())

        // Instance-specific instructions
        runBlocking {
            poster.postMessage("16134", "Ktor bot started")
        }

        Tasks.schedule("VBA star race", Tasks.dailyUTC(23, 45)) {
            val rubberDuck = hookString.repo("rubberduck-vba/Rubberduck") to gitHubApi.stars("rubberduck-vba/Rubberduck")
            val oletools = hookString.repo("decalage2/oletools") to gitHubApi.stars("decalage2/oletools")
            val list = listOf(rubberDuck, oletools).joinToString(" vs. ") {
                it.first + ": ${it.second} stars"
            }
            poster.postMessage("14929", list)
        }

        Tasks.schedule("REFRESH", Tasks.utcMidnight) { poster.postMessage("16134", "REFRESH!") }

        logger.info("Ready")
    }

    private fun readSecret(fileName: String): String = File("$fileName.secret").readText().trim()
}

fun main() {
    DugaMain.start()
}
