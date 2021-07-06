package net.zomis.duga

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.runBlocking
import net.zomis.duga.chat.*
import net.zomis.duga.server.ArgumentsCheck
import net.zomis.duga.server.DugaServer
import net.zomis.duga.tasks.Tasks
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import net.zomis.duga.utils.stats.DugaStatsDynamoDB
import net.zomis.duga.utils.stats.DugaStatsInternalMap
import net.zomis.duga.utils.stats.DugaStatsNoOp
import org.slf4j.LoggerFactory
import java.io.File
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit

object DugaMain {
    private val logger = LoggerFactory.getLogger(DugaMain::class.java)

    fun start(params: Array<String>) {
        // Dependencies and basic setup
        val args = ArgumentsCheck(params.toSet())
        val botConfig = jacksonObjectMapper().readValue(File("bot.secret"), BotConfig::class.java)
        val client = DugaClient()
        val bot = DugaBot(client.client, botConfig) { httpClient, config ->
            val se = StackExchangeLogin(httpClient, config)
            if (se.login()) {
                se.fkeyReal()
            } else throw RuntimeException()
        }
        val poster = if (args.contains("duga-poster")) DugaPosterImpl(bot) else LoggingPoster()
        val stats = when {
            args.contains("stats-local") -> DugaStatsInternalMap()
            args.contains("stats-dynamodb") -> DugaStatsDynamoDB()
            else -> DugaStatsNoOp()
        }

        val gitHubApi = GitHubApi(client.client, readSecret("github"))
        val stackExchangeApi = StackExchangeApi(client.client, readSecret("stackexchange"))
        val hookString = HookString(stats, gitHubApi)

        DugaServer(poster, gitHubApi, stackExchangeApi, stats, hookString).start(args)
        logger.info("Ready")
    }

    private fun readSecret(fileName: String): String = File("$fileName.secret").readText().trim()
}

fun main(args: Array<String>) {
    DugaMain.start(args)
}
