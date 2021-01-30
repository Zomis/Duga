package net.zomis.duga

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import net.zomis.duga.chat.*
import net.zomis.duga.tasks.Tasks
import java.io.File

object DugaMain {
    fun start() {
//        DugaServer.start()

        val client = DugaClient(jacksonObjectMapper().readValue(File("bot.secret"), BotConfig::class.java))
        val bot = DugaBot(client.client, client.config) { httpClient, botConfig ->
            val se = StackExchangeLogin(httpClient, botConfig)
            if (se.login()) {
                se.fkeyReal()
            } else throw RuntimeException()
        }
        runBlocking {
            val tasks = Tasks(DugaPoster(bot))
//            tasks.startTask()
            tasks.midnight()

//            DugaPoster(bot).postMessage("16134", "Hello!")
        }
    }
}

fun main() {
    DugaMain.start()
}