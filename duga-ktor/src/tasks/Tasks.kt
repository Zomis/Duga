package net.zomis.duga.tasks

import com.github.shyiko.skedule.Schedule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.time.*
import java.time.temporal.ChronoUnit

object Tasks {

    private val logger = LoggerFactory.getLogger(Tasks::class.java)

    val utcMidnight = Schedule.at(LocalTime.from(Instant.now().truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()))).everyDay()
    // val schedule = Schedule.parse("every minute")

    fun schedule(name: String, schedule: Schedule, task: suspend () -> Unit): Job {
        val times = schedule.iterate(ZonedDateTime.now())
        return GlobalScope.launch {
            while (true) {
                sleepUntil(name, times.next())
                logger.info("Task $name: Executing")
                task()
            }
        }
    }

    private suspend fun sleepUntil(name: String, next: ZonedDateTime) {
        val duration = Duration.between(ZonedDateTime.now(), next)
        println("Task $name: Next is at $next, sleeping for $duration")
        delay(duration.toMillis())
    }

}