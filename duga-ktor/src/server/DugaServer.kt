package net.zomis.duga.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.shyiko.skedule.Schedule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.launch
import net.zomis.duga.DugaMain
import net.zomis.duga.DugaTasks
import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.features.DugaFeatures
import net.zomis.duga.features.StackExchange
import net.zomis.duga.server.webhooks.AppVeyorWebhook
import net.zomis.duga.server.webhooks.GitHubWebhook
import net.zomis.duga.server.webhooks.SplunkWebhook
import net.zomis.duga.server.webhooks.StatsWebhook
import net.zomis.duga.tasks.Tasks
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import net.zomis.duga.utils.stats.DugaStats
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

class ArgumentsCheck(private val args: Collection<String>) {
    private val logger = LoggerFactory.getLogger(DugaMain::class.java)

    suspend fun check(arg: String, block: suspend () -> Unit) {
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

class DugaServer(
    private val poster: DugaPoster,
    private val gitHubApi: GitHubApi,
    private val stackExchangeApi: StackExchangeApi,
    private val stats: DugaStats
) {
    val dugaTasks = DugaTasks(poster, stackExchangeApi)

    fun start(args: ArgumentsCheck) {
        embeddedServer(Netty, port = 3842) {
            val application = this
            val hookString = HookString(stats, gitHubApi, application)

            val tasks = Tasks()
            install(CORS) {
                allowHost("stats.zomis.net", schemes = listOf("https"))
            }
            install(ContentNegotiation) {
                jackson()
            }
            install(CallLogging) {
                level = Level.INFO
                filter { call -> call.request.path().startsWith("/") }
            }
            routing {
                get("/") {
                    call.respondText("Hello, Ktor!")
                }
                tasks.route(this)
                SplunkWebhook.route(this, poster)
                AppVeyorWebhook.route(this, poster)
                val statsConfig = File("stats.secret").let {
                    if (it.exists()) jacksonObjectMapper().readValue(it, StatsWebhook.Config::class.java) else StatsWebhook.Config()
                }
                StatsWebhook.route(this, stats, statsConfig)
                GitHubWebhook(poster, hookString).route(this, application)
            }

            launch {
                // Instance-specific instructions
                args.check("hello-world") {
                    poster.postMessage("16134", "Ktor bot started")
                }

                args.check("weekly-update-reminder") {
                    tasks.schedule(this, "Weekly update", Tasks.weeklyUTC(16, 0, setOf(DayOfWeek.MONDAY))) {
                        StackExchange(poster).weeklyUpdate()
                    }
                }

                args.check("vba-star-race") {
                    tasks.schedule(this, "VBA star race", Tasks.dailyUTC(23, 45)) {
                        StackExchange(poster).starRace(hookString, gitHubApi, listOf("rubberduck-vba/Rubberduck", "decalage2/oletools"))
                    }
                }

                args.check("refresh") {
                    tasks.schedule(this, "REFRESH", Tasks.utcMidnight) { poster.postMessage("16134", "***REFRESH!***") }
                }
                args.check("comment-scan") {
                    // TEST: Needs saving of machine learning parameters for migration to lambda
                    val commentsScanTask = dugaTasks.commentsScanTask()
                    tasks.schedule(this, "Comments scanning", Schedule.every(1, ChronoUnit.MINUTES)) {
                        commentsScanTask.run(this)
                    }
                }
                args.check("answer-invalidation") {
                    tasks.schedule(this, "Invalidation checks", Schedule.every(5, ChronoUnit.MINUTES), dugaTasks::answerInvalidation)
                }
                args.check("unanswered") {
                    tasks.schedule(this, "Unanswered CR", Tasks.utcMidnight) {
                        StackExchange(poster).codeReviewUnanswered(stackExchangeApi)
                    }
                }
                args.check("daily-stats") {
                    tasks.schedule(this, "Daily stats", Tasks.utcMidnight) {
                        DugaFeatures(poster).dailyStats(stats, clearStats = true)
                    }
                }
            }
        }.start(true)
    }

}
