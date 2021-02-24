package net.zomis.duga.utils.stackexchange

import net.zomis.duga.chat.DugaPoster
import java.time.Instant
import java.time.temporal.ChronoUnit

class QuestionScanTask(val poster: DugaPoster, val stackApi: StackExchangeApi, val site: String) {

    private val FILTER = "!DEQ-Ts0KBm6n14zYUs8UZUsw.yj0rZkhsEKF2rI4kBp*yOHv4z4"
    val LATEST_QUESTIONS = "questions?order=desc&sort=activity"

    private var lastCheck: Instant = Instant.now().minus(1, ChronoUnit.MINUTES)

    suspend fun run() {
        val previousCheck = this.lastCheck
        this.lastCheck = Instant.now()
        val questions = stackApi.apiCall(LATEST_QUESTIONS, site, FILTER)
        AnswerInvalidationCheck.perform(poster, questions, previousCheck, stackApi)
    }

}
