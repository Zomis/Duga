package net.zomis.duga.tasks

import net.zomis.duga.aws.DugaMessage
import net.zomis.duga.utils.StackExchangeAPI
import org.slf4j.LoggerFactory
import java.io.IOException

class UnansweredTask(private val room: String, private val site: String, private val message: String) : DugaTask {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val api = StackExchangeAPI()

    override fun perform(): List<DugaMessage> {
        return try {
            val result = api.apiCall("info", site, "default")
            val unanswered = result["items"][0]["total_unanswered"].asInt()
            val total = result["items"][0]["total_questions"].asInt()
            val percentageAnswered = (total.toDouble() - unanswered.toDouble()) / total.toDouble()
            val percentageStr = String.format("%.4f", percentageAnswered * 100)
            var send = message
            send = send.replace("%unanswered%", unanswered.toString())
            send = send.replace("%percentage%", percentageStr)
            listOf(DugaMessage(room, send))
        } catch (e: IOException) {
            logger.error("Error with StackExchange API Call", e)
            listOf()
        }
    }

}
