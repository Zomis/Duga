package net.zomis.duga.chat

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*

class DugaClient(val config: BotConfig) {

    val client = HttpClient(Apache) {
        install(WebSockets)
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(HttpRedirect) {
            checkHttpMethod = false // See https://github.com/ktorio/ktor/issues/1793#issuecomment-613862691
        }
    }

}