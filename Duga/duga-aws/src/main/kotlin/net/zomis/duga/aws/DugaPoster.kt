package net.zomis.duga.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import net.zomis.duga.chat.BotConfiguration
import net.zomis.duga.chat.ChatBot
import net.zomis.duga.chat.StackExchangeChatBot
import net.zomis.duga.chat.events.DugaLoginEvent

class DugaPoster : RequestHandler<Map<String, Any>, Map<String, Any>> {

    override fun handleRequest(input: Map<String, Any>?, context: Context?): Map<String, Any> {
        val messages = readSQSTriggerInput(input!!)

        val dynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.EU_CENTRAL_1)
            .build()

        val bot = createBot()

        val state = DugaState.readFromDB(dynamoDB)
        if (state != null) {
            bot.load(state)
        }
        bot.registerListener(DugaLoginEvent::class.java) {
            // Save state when bot successfully logs in, to reduce number of logins required
            val savedState = bot.saveState()
            DugaState.saveToDB(dynamoDB, savedState)
            println("Saved bot state")
        }

        if (messages != null) {
            postMessages(bot, messages)
        } else {
            processSQSQueue(bot)
        }

        return mapOf("result" to "done")
    }

    private fun readSQSTriggerInput(input: Map<String, Any>): List<DugaMessage>? {
        if (input.isEmpty()) {
            return null
        }
        val messages = input["Records"] as List<Map<String, Any>>
        val dugaMessages = messages.map {
            val body = it["body"] as String
            val attributes = it["attributes"] as Map<String, Any>
            val room = attributes["MessageGroupId"] as String
            DugaMessage(room, body)
        }
        return dugaMessages
    }

    private fun postMessages(bot: ChatBot, messages: List<DugaMessage>) {
        messages.forEach { postMessage(bot, it) }
    }

    fun postMessage(bot: ChatBot, it: DugaMessage) {
        val response = bot.postNow(bot.room(it.room).message(it.message))
        if (response.hasException()) {
            response.exception.printStackTrace()
            throw Exception("Unable to post message", response.exception)
        } else {
            println("Posted id ${response.id} at ${response.time}: ${response.fullResponse}")
        }
    }

    private fun processSQSQueue(bot: ChatBot) {
        DugaSQS().fetch { postMessage(bot, it) }
    }

    private fun createBot(): StackExchangeChatBot {
        val username = System.getenv("SE_USERNAME")
        val secretPassword = System.getenv("SE_PASSWORD")
        val config = BotConfiguration()
        config.botEmail = username
        config.botPassword = secretPassword
        config.chatUrl = "https://chat.stackexchange.com"
        config.rootUrl = "https://meta.stackexchange.com"
        val bot = StackExchangeChatBot(config)
        println("Created bot for e-mail ${config.botEmail}")
        return bot
    }

}