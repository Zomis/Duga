package net.zomis.duga

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.runBlocking
import net.zomis.duga.chat.*
import net.zomis.duga.server.DugaServer
import net.zomis.duga.tasks.Tasks
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import net.zomis.duga.utils.stats.DugaStatsInternalMap
import net.zomis.duga.utils.stats.DugaStatsNoOp
import org.slf4j.LoggerFactory
import java.io.File
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit

object DugaMain {
    private val logger = LoggerFactory.getLogger(DugaMain::class.java)

    class ArgumentsCheck(private val args: Collection<String>) {
        fun check(arg: String, block: () -> Unit) {
            if (contains(arg)) {
                block()
            }
        }

        fun contains(arg: String): Boolean {
            val found = args.contains(arg)
            logger.info("Checking for argument \"{}\", found? {}", arg, found)
            return found
        }
    }

    fun start(params: Array<String>) {
        // Dependencies and basic setup
        val args = ArgumentsCheck(params.toSet())
        val client = DugaClient(jacksonObjectMapper().readValue(File("bot.secret"), BotConfig::class.java))
        val bot = DugaBot(client.client, client.config) { httpClient, botConfig ->
            val se = StackExchangeLogin(httpClient, botConfig)
            if (se.login()) {
                se.fkeyReal()
            } else throw RuntimeException()
        }
        val poster = if (args.contains("duga-poster")) DugaPosterImpl(bot) else LoggingPoster()
        val stats = if (args.contains("local-stats")) DugaStatsInternalMap() else DugaStatsNoOp()

        val gitHubApi = GitHubApi(client.client, readSecret("github"))
        val stackExchangeApi = StackExchangeApi(client.client, readSecret("stackexchange"))
        val hookString = HookString(stats, gitHubApi)
        val dugaTasks = DugaTasks(poster, stackExchangeApi)

        // Instance-specific instructions
        args.check("hello-world") {
            runBlocking {
                poster.postMessage("16134", "Ktor bot started")
            }
        }

        args.check("weekly-update-reminder") {
            Tasks.schedule("Weekly update", Tasks.weeklyUTC(17, 0, setOf(DayOfWeek.TUESDAY))) {
                poster.postMessage("16134", "Has @Simon posted his weekly update?")
            }
        }

        args.check("vba-star-race") {
            Tasks.schedule("VBA star race", Tasks.dailyUTC(23, 45)) {
                val rubberDuck = hookString.repo("rubberduck-vba/Rubberduck") to gitHubApi.stars("rubberduck-vba/Rubberduck")
                val oletools = hookString.repo("decalage2/oletools") to gitHubApi.stars("decalage2/oletools")
                val list = listOf(rubberDuck, oletools).joinToString(" vs. ") {
                    it.first + ": ${it.second} stars"
                }
                poster.postMessage("14929", list)
            }
        }

        args.check("refresh") {
            Tasks.schedule("REFRESH", Tasks.utcMidnight) { poster.postMessage("16134", "***REFRESH!***") }
        }
        args.check("comment-scan") {
            Tasks.schedule("Comments scanning", Schedule.every(1, ChronoUnit.MINUTES), dugaTasks::commentScan)
        }
        args.check("answer-invalidation") {
            Tasks.schedule("Invalidation checks", Schedule.every(5, ChronoUnit.MINUTES), dugaTasks::answerInvalidation)
        }
        args.check("daily-stats") {
            Tasks.schedule("Daily stats", Tasks.utcMidnight) {
                val allStats = stats.allStats()
                val messages = allStats.map { stat ->
                    val values = stat.reset()
                    val group = stat.group
                    val url = stat.url
                    "\\[[**$group**]($url)\\] $values"
                }
                val rooms = listOf("16134", "14929")
                rooms.forEach { room ->
                    val roomPoster = poster.room(room)
                    roomPoster.post("***REFRESH!***")
                    messages.forEach { message ->
                        roomPoster.post(message)
                    }
                }
            }
        }

        DugaServer(poster, gitHubApi, hookString).start()

        logger.info("Ready")
    }

    private fun readSecret(fileName: String): String = File("$fileName.secret").readText().trim()
}

fun main(args: Array<String>) {
    DugaMain.start(args)
}
