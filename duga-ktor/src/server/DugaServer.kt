package net.zomis.duga.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.server.webhooks.GitHubWebhook
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import org.slf4j.event.Level

class DugaServer(
    private val poster: DugaPoster,
    private val gitHubApi: GitHubApi,
    private val hookString: HookString
) {

    fun start() {
        embeddedServer(Netty, port = 3842) {
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
                GitHubWebhook(poster, gitHubApi, hookString).route(this)
            }
        }.start(false)
    }

}
