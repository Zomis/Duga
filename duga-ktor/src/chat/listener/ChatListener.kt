package net.zomis.duga.chat.listener

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import net.zomis.duga.chat.DugaBot

class ChatListener(val duga: DugaBot) {

    suspend fun listenCheck(room: String) {
        val data = duga.httpClient.post<String>(duga.chatUrl + "/chats/$room/events") {
            body = FormDataContent(Parameters.build {
                append("fkey", duga.fkey())
                append("mode", "messages")
                append("msgCount", "10")
            })
        }
        println(data)
    }

    private data class UrlJson(val url: String)
    suspend fun listenerWebsocket(room: String) {
        val urlJson = duga.httpClient.post<String>(duga.chatUrl + "/ws-auth") {
            body = FormDataContent(Parameters.build {
                append("fkey", duga.fkey())
                append("roomid", room)
            })
        }
        println("Url Json: $urlJson")
        val url = jacksonObjectMapper().readValue(urlJson, UrlJson::class.java)
        println("Url Json: $url")

        val lastPart = url.url.split("/").last()
        val client2 = HttpClient(CIO).config { install(WebSockets) }
        client2.ws("${url.url}?l=121103004", {}) {
//        httpClient.ws(method = HttpMethod.Get, host = "chat.sockets.stackexchange.com", port = 433, path = "/events/$room/$lastPart", {}) {
            println("Connected")
            for (msg in incoming) {
                println("incoming: $msg")
                if (msg is Frame.Text) {
                    println("Server said: " + msg.readText())
                }
            }
            println("End of transmission")
        }
    }

}