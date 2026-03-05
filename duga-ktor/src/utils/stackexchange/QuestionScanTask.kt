package net.zomis.duga.utils.stackexchange

import net.zomis.duga.chat.DugaPoster
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

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
        val nowNow = Instant.now()
        val questions = stackApi.apiCall(LATEST_QUESTIONS, site, FILTER)
        val questionLastActivity = questions?.get("items")?.map { it.get("last_activity_date")?.asLong() ?: 0 }?.maxOrNull() ?: 0
        logger.info("lastCheck highest post time is $questionLastActivity, previous lastCheck is ${previousCheck.epochSecond}")
        data.lastCheck = Instant.ofEpochSecond(questionLastActivity)
        AnswerInvalidationCheck.perform(poster, questions, previousCheck, stackApi, nowNow)
        data.lastCheck = nowNow.minus(11.minutes.toJavaDuration())
        data.save()
    }

}
