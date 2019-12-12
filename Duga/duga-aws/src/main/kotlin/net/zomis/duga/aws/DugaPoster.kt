package net.zomis.duga.aws

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import net.zomis.duga.chat.BotConfiguration
import net.zomis.duga.chat.ChatBot
import net.zomis.duga.chat.StackExchangeChatBot
import net.zomis.duga.chat.events.DugaLoginEvent

class DugaPoster : RequestHandler<String, String> {

    override fun handleRequest(input: String?, context: Context?): String {
        val dynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.EU_CENTRAL_1)
            .build()

        val bot = createBot()

        val state = DugaState.readFromDB(dynamoDB)
        if (state != null) {
            bot.load(state)
        }
        bot.registerListener(DugaLoginEvent::class.java) {
            val savedState = bot.saveState()
            DugaState.saveToDB(dynamoDB, savedState)
            println("Saved bot state")
        }

        processMessages(bot)

        /*
        Load 1 kb string
        Apply string to bot if it exists
        Send messages - if message fails, then login again.
          When logging in again, save new state.
        Empty queue and then terminate.
        */

/*
        xxxxxxx start, wait for bot to login
        store bot in global storage - outside this class, to potentially re-use it
        look into this:
        https://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-parameter-store.html
        bot.start()
        */
        return "Done"
    }

    private fun processMessages(bot: ChatBot) {
        FetchMessage().fetch {
            val response = bot.postNow(bot.room(it.room).message(it.message))
            if (response.hasException()) {
                response.exception.printStackTrace()
                throw Exception("Unable to post message", response.exception)
            } else {
                println("Posted id ${response.id} at ${response.time}: ${response.fullResponse}")
            }
        }
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