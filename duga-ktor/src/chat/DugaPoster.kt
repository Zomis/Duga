package net.zomis.duga.chat

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.MessageAttributeValue
import aws.sdk.kotlin.services.sqs.sendMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.Instant

interface RoomPoster {
    suspend fun post(message: String)
    fun postAsync(scope: CoroutineScope, message: String)
}

class DugaRoomPoster(private val poster: DugaPoster, val room: String): RoomPoster {
    override suspend fun post(message: String) {
        poster.postMessage(room, message)
    }

    override fun postAsync(scope: CoroutineScope, message: String) {
        scope.launch {
            post(message)
        }
    }
}

interface DugaPoster {
    suspend fun postMessage(room: String, message: String): PostResult?
    fun room(room: String) = DugaRoomPoster(this, room)

}

class LoggingPoster: DugaPoster {
    private val logger = LoggerFactory.getLogger(LoggingPoster::class.java)

    override suspend fun postMessage(room: String, message: String): PostResult? {
        logger.info("$room: $message")
        return PostResult(0, Instant.now().epochSecond)
    }

}

class SqsPoster(private val sqsQueue: String) : DugaPoster {

    override suspend fun postMessage(room: String, message: String): PostResult? {
        println("SQS Poster $room: $message")
        SqsClient { region = "eu-central-1" }.use { sqs ->
            val response = sqs.sendMessage {
                queueUrl = sqsQueue
                messageGroupId = room
                messageAttributes = mapOf(
                    "room" to MessageAttributeValue {
                        dataType = "String"
                        stringValue = room
                    }
                )
                messageBody = message
            }
//            response.messageId
            return PostResult(0, Instant.now().epochSecond)
        }
    }

}

data class PostResult(val id: Long, val time: Long)
class DugaPosterImpl(val duga: DugaBot): DugaPoster {

    private val logger = LoggerFactory.getLogger(DugaPoster::class.java)
    private val mapper = jacksonObjectMapper()

    private suspend fun chatFormParams(message: String) = Parameters.build {
        append("text", message)
        append("fkey", duga.fkey())
    }

    private suspend fun internalPost(room: String, message: String): PostResult? {
        logger.info("Message to room $room: $message")
        try {
            val result = duga.httpClient.submitForm(duga.chatUrl + "/chats/$room/messages/new", chatFormParams(message))
            logger.info("Post result: $result")
            return result.body<PostResult>()
        } catch (e: ClientRequestException) {
            if (e.message?.contains("You can perform this action again in") == true) {
                val performAgainIn = e.message!!.substringAfter("You can perform this action again in").trim()
                logger.info("perform again in $performAgainIn")
                if (performAgainIn.substringAfter(' ').contains("second")) {
                    delay(performAgainIn.substringBefore(' ').toLong() * 1000)
                    return internalPost(room, message)
                }
            }
            throw e
        }
    }

    override suspend fun postMessage(room: String, message: String): PostResult? {
        return try {
            internalPost(room, message)
        } catch (e: Exception) {
            logger.warn("Unable to post '$message' to room $room. Retrying in a little while", e)
            delay(10_000L)
            duga.refreshFKey()
            try {
                internalPost(room, message)
            } catch (e: Exception) {
                logger.error("Retry failed. Unable to post '$message' to room $room", e)
                return null
            }
        }
    }

    suspend fun editMessage(messageId: Long, newText: String): Boolean {
        val result = duga.httpClient.submitForm(duga.chatUrl + "/messages/$messageId", chatFormParams(newText))
        logger.info("Edit $messageId to '$newText': $result")
        return result.body<String>().trim() == "ok"
    }

    suspend fun getMessage(messageId: Long): String {
        return duga.httpClient.get(duga.chatUrl + "/message/$messageId?plain=true").body() // &_=timestampMs
    }

}
