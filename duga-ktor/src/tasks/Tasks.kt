package net.zomis.duga.tasks

import com.github.shyiko.skedule.Schedule
import io.ktor.util.date.*
import kotlinx.coroutines.delay
import net.zomis.duga.chat.DugaPoster
import java.time.*
import java.time.temporal.ChronoUnit

class Tasks(val dugaPoster: DugaPoster) {

    suspend fun schedule(schedule: Schedule, task: () -> Unit) {
    }

    suspend fun midnight() {
        val now = ZonedDateTime.now()
        val s = Schedule.at(LocalTime.from(Instant.now().truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()))).everyDay().iterate(now)
        sleepUntil(s.next())

    }

    suspend fun sleepUntil(next: ZonedDateTime) {
        val duration = Duration.between(ZonedDateTime.now(), next)
        println("Next is at $next, sleeping $duration")
        delay(duration.toMillis())
    }

    suspend fun startTask() {
        while (true) {
            val now = ZonedDateTime.now()
            val instant = Instant.now().truncatedTo(ChronoUnit.DAYS).toGMTDate()
            println(instant)
            println(LocalTime.from(Instant.now().truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault())))

            val next = Schedule.at(LocalDateTime.now().toLocalDate().atStartOfDay().toLocalTime()).everyDay().next(now)
//                        val next = Schedule.parse("every minute").next(now)
        }
    }

}