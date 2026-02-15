package net.zomis.duga.chat

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.HttpHeaders.ContentEncoding
import io.ktor.serialization.jackson.jackson
import io.netty.handler.codec.compression.StandardCompressionOptions.gzip

class DugaClient {

    val client = HttpClient(Apache) {
        install(ContentEncoding) {
            gzip()
        }
        install(ContentNegotiation) {
            jackson()
        }
        install(WebSockets)
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(HttpRedirect) {
            checkHttpMethod = false // See https://github.com/ktorio/ktor/issues/1793#issuecomment-613862691
        }
    }

}