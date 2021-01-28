package net.zomis.duga.server

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.event.Level

object DugaServer {

    fun start() {
        embeddedServer(Netty, port = 8042) {
            install(CallLogging) {
                level = Level.INFO
                filter { call -> call.request.path().startsWith("/") }
            }
            routing {
                get("/") {
                    call.respondText("Hello, Ktor!")
                }
            }
        }.start(false)
    }

}
