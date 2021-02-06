package net.zomis.duga.tasks

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.*
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

object Tasks {

    private val logger = LoggerFactory.getLogger(Tasks::class.java)

    val utcMidnight = Schedule.at(LocalTime.from(Instant.now().truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()))).everyDay()
    // val schedule = Schedule.parse("every minute")

    fun schedule(name: String, schedule: Schedule, task: suspend () -> Unit): Job {
        val times = schedule.iterate(ZonedDateTime.now()).iterator()
        return GlobalScope.launch {
            for (time in times) {
                sleepUntil(name, time)
                logger.info("Task $name: Executing")
                task()
            }
        }
    }

    fun once(name: String, task: suspend () -> Unit): Job {
        return GlobalScope.launch {
            logger.info("Task $name: Executing")
            task()
        }
    }

    private suspend fun sleepUntil(name: String, next: ZonedDateTime) {
        val now = ZonedDateTime.now()
        val duration = Duration.between(now, next)
        if (duration.isNegative) {
            throw IllegalStateException("Trying to sleep for a negative amount of time: $duration between $now and $next")
        }
        println("Task $name: Next is at $next, sleeping for $duration")
        delay(duration.toMillis())
    }

    fun daily(hour: Int, minute: Int): Schedule {
        return Schedule.at(LocalTime.of(hour, minute)).everyDay()
    }

    fun dailyUTC(hour: Int, minute: Int): Schedule {
        return Schedule.at(LocalTime.from(Instant.now().truncatedTo(ChronoUnit.DAYS)
            .plus(hour.toLong(), ChronoUnit.HOURS)
            .plus(minute.toLong(), ChronoUnit.MINUTES)
            .atZone(ZoneId.systemDefault()))).everyDay()
    }

}