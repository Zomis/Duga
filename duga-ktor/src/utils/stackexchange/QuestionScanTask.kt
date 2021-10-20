package net.zomis.duga.utils.stackexchange

import net.zomis.duga.chat.DugaPoster
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit

class QuestionScanTask(val poster: DugaPoster, val stackApi: StackExchangeApi, val site: String) {

    private val FILTER = "!DEQ-Ts0KBm6n14zYUs8UZUsw.yj0rZkhsEKF2rI4kBp*yOHv4z4"
    val LATEST_QUESTIONS = "questions?order=desc&sort=activity"

    private var logger = LoggerFactory.getLogger(this::class.java)
    private var lastCheck: Instant = Instant.now().minus(1, ChronoUnit.MINUTES)

    suspend fun run() {
        val previousCheck = this.lastCheck
        this.lastCheck = Instant.now()
        val questions = stackApi.apiCall(LATEST_QUESTIONS, site, FILTER)
        val t = questions?.get("items")?.map { it.get("creation_date")?.asLong() ?: 0 }?.maxOrNull() ?: 0
        logger.info("lastCheck highest post time is {}, previous lastCheck is {}", t, lastCheck.epochSecond)
        this.lastCheck = Instant.ofEpochSecond(maxOf(lastCheck.epochSecond, t))
        AnswerInvalidationCheck.perform(poster, questions, previousCheck, stackApi)
    }

}
