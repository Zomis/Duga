package net.zomis.duga.tasks

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.time.*
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

object Tasks {

    private val logger = LoggerFactory.getLogger(Tasks::class.java)

    val utcMidnight: Schedule = Schedule.at(LocalTime.from(Instant.now().truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()))).everyDay()
    // val schedule = Schedule.parse("every minute")

    suspend fun schedule(scope: CoroutineScope, name: String, schedule: Schedule, task: suspend () -> Unit): Job {
        val times = schedule.iterate(ZonedDateTime.now()).iterator()
        return scope.launch {
            try {
                for (time in times) {
                    sleepUntil(name, time)
                    logger.info("Task $name: Executing")
                    if (!this.isActive) {
                        logger.info("Task $name interrupted")
                        return@launch
                    }
                    try {
                        task()
                    } catch (e: Exception) {
                        logger.error("Task Exception in $name, trying again next time", e)
                    }
                }
                logger.info("Task $name: Finished")
            } catch (e: Exception) {
                logger.error("Task Error in $name, aborting task", e)
            }
        }
    }

    fun once(name: String, task: suspend () -> Unit): Job {
        return GlobalScope.launch {
            try {
                logger.info("Task $name: Executing")
                task()
            } catch (e: Exception) {
                logger.error("Task Exception in $name", e)
            }
        }
    }

    private suspend fun sleepUntil(name: String, next: ZonedDateTime) {
        val now = ZonedDateTime.now()
        val duration = Duration.between(now, next)
        if (duration.isNegative) {
            throw IllegalStateException("Trying to sleep for a negative amount of time: $duration between $now and $next")
        }
        logger.info("Task $name: Next is at $next, sleeping for $duration")
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

    fun weeklyUTC(hour: Int, minute: Int, weekDays: Set<DayOfWeek>): Schedule {
        return Schedule.at(LocalTime.from(Instant.now().truncatedTo(ChronoUnit.DAYS)
                .plus(hour.toLong(), ChronoUnit.HOURS)
                .plus(minute.toLong(), ChronoUnit.MINUTES)
                .atZone(ZoneId.systemDefault()))).every(weekDays)
    }


}