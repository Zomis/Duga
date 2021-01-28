package net.zomis.duga.chat

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class DugaPoster(val duga: DugaBot) {

    suspend fun postMessage(room: String, message: String) {
        val result = duga.httpClient.post<String>(duga.chatUrl + "/chats/$room/messages/new") {
            body = FormDataContent(Parameters.build {
                append("text", message)
                append("fkey", duga.fkey())
            })
        }
        println(result)
    }

}