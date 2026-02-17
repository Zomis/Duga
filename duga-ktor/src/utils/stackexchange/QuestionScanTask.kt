package net.zomis.duga.utils.stackexchange

import net.zomis.duga.chat.DugaPoster
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit

class QuestionScanTask(
    val poster: DugaPoster,
    val stackApi: StackExchangeApi,
    val site: String,
    val data: AnswerInvalidationCheckData,
) {

    private val FILTER = "!DEQ-Ts0KBm6n14zYUs8UZUsw.yj0rZkhsEKF2rI4kBp*yOHv4z4"
    val LATEST_QUESTIONS = "questions?order=desc&sort=activity"

    private var logger = LoggerFactory.getLogger(this::class.java)

    suspend fun run() {
        data.load()
        val previousCheck = data.lastCheck
        data.lastCheck = Instant.now()
        val questions = stackApi.apiCall(LATEST_QUESTIONS, site, FILTER)
        val t = questions?.get("items")?.map { it.get("creation_date")?.asLong() ?: 0 }?.maxOrNull() ?: 0
        logger.info("lastCheck highest post time is {}, previous lastCheck is {}", t, data.lastCheck.epochSecond)
        data.lastCheck = Instant.ofEpochSecond(maxOf(data.lastCheck.epochSecond, t))
        AnswerInvalidationCheck.perform(poster, questions, previousCheck, stackApi)
        data.save()
    }

}
