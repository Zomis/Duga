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
                        val days = ChronoUnit.DAYS.between(LocalDate.of(1986, Month.SEPTEMBER, 22), LocalDate.now())
                        val week = days / 7
                        poster.postMessage("16134", "Has @Simon posted his weekly update? (Week $week, $days days)")
                    }
                }

                args.check("vba-star-race") {
                    tasks.schedule(this, "VBA star race", Tasks.dailyUTC(23, 45)) {
                        val rubberDuck = hookString.repo("rubberduck-vba/Rubberduck") to gitHubApi.stars("rubberduck-vba/Rubberduck")
                        val oletools = hookString.repo("decalage2/oletools") to gitHubApi.stars("decalage2/oletools")
                        val list = listOf(rubberDuck, oletools).joinToString(" vs. ") {
                            it.first + " ${it.second} stars"
                        }
                        poster.postMessage("14929", list)
                    }
                }

                args.check("refresh") {
                    tasks.schedule(this, "REFRESH", Tasks.utcMidnight) { poster.postMessage("16134", "***REFRESH!***") }
                }
                args.check("comment-scan") {
                    val commentsScanTask = dugaTasks.commentsScanTask(this)
                    tasks.schedule(this, "Comments scanning", Schedule.every(1, ChronoUnit.MINUTES), commentsScanTask::run)
                }
                args.check("answer-invalidation") {
                    tasks.schedule(this, "Invalidation checks", Schedule.every(5, ChronoUnit.MINUTES), dugaTasks::answerInvalidation)
                }
                args.check("unanswered") {
                    tasks.schedule(this, "Unanswered CR", Tasks.utcMidnight) {
                        val siteStats = stackExchangeApi.unanswered("codereview")
                        val percentageStr = String.format("%.4f", siteStats.percentageAnswered() * 100)
                        val message = "***REFRESH!*** There are ${siteStats.unanswered} unanswered questions ($percentageStr answered)"
                        poster.postMessage("8595", message)
                    }
                }
                args.check("daily-stats") {
                    tasks.schedule(this, "Daily stats", Tasks.utcMidnight) {
                        val allStats = stats.clearStats()
                        val messages = allStats.map { stat ->
                            val values = stat.reset().toList()
                                .joinToString(". ") { "${it.second} ${it.first}" }
                            val group = stat.displayName
                            val url = stat.url
                            "\\[[**$group**]($url)\\] $values"
                        }
                        val rooms = listOf("16134")
                        rooms.forEach { room ->
                            val roomPoster = poster.room(room)
                            roomPoster.post("***REFRESH!***")
                            messages.forEach { message ->
                                roomPoster.post(message)
                            }
                        }
                    }
                }
            }
        }.start(true)
    }

}
