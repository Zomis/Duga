package net.zomis.duga.chat

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.slf4j.LoggerFactory

class DugaPoster(val duga: DugaBot) {

    private val logger = LoggerFactory.getLogger(DugaPoster::class.java)
    private val mapper = jacksonObjectMapper()

    data class PostResult(val id: Long, val time: Long)
    private suspend fun internalPost(room: String, message: String): PostResult? {
        val result = duga.httpClient.post<String>(duga.chatUrl + "/chats/$room/messages/new") {
            body = FormDataContent(Parameters.build {
                append("text", message)
                append("fkey", duga.fkey())
            })
        }
        logger.info("Post result: $result")
        return mapper.readValue(result, PostResult::class.java)
    }

    suspend fun postMessage(room: String, message: String): PostResult? {
        return try {
            internalPost(room, message)
        } catch (e: Exception) {
            logger.warn("Unable to post '$message' to room $room. Retrying", e)
            duga.refreshFKey()
            try {
                internalPost(room, message)
            } catch (e: Exception) {
                logger.error("Retry failed. Unable to post '$message' to room $room", e)
                return null
            }
        }
    }

}
