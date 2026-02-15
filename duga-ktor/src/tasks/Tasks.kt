package net.zomis.duga.tasks

import com.github.shyiko.skedule.Schedule
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.time.*
import java.time.temporal.ChronoUnit

class TaskExecutions {
    data class TaskExecutionData(val name: String, var next: ZonedDateTime? = null, var last: ZonedDateTime? = null, var count: Int = 0)
    private val tasks: MutableMap<String, TaskExecutionData> = mutableMapOf()
    private val lock = Any()

    fun get(): Map<String, TaskExecutionData> {
        synchronized(lock) {
            return tasks.toMap()
        }
    }

    fun next(name: String, time: ZonedDateTime) {
        synchronized(lock) {
            tasks.computeIfAbsent(name) { TaskExecutionData(name) }
            tasks.getValue(name).next = time
        }
    }

    fun add(name: String) {
        synchronized(lock) {
            tasks.computeIfAbsent(name) { TaskExecutionData(name) }
            val taskData = tasks.getValue(name)
            taskData.count++
            taskData.last = ZonedDateTime.now()
        }
    }
}

class Tasks {

    companion object {
        val utcMidnight: Schedule = Schedule.at(LocalTime.from(Instant.now().truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()))).everyDay()

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

    private val taskExecutions: TaskExecutions = TaskExecutions()

    private val logger = LoggerFactory.getLogger(Tasks::class.java)

    suspend fun schedule(scope: CoroutineScope, name: String, schedule: Schedule, task: suspend () -> Unit): Job {
        val times = schedule.iterate(ZonedDateTime.now()).iterator()
        return scope.launch {
            try {
                for (time in times) {
                    taskExecutions.next(name, time)
                    sleepUntil(name, time)
                    logger.info("Task $name: Executing")
                    if (!this.isActive) {
                        logger.info("Task $name interrupted")
                        return@launch
                    }
                    try {
                        task()
                        taskExecutions.add(name)
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

    private suspend fun sleepUntil(name: String, next: ZonedDateTime) {
        val now = ZonedDateTime.now()
        val duration = Duration.between(now, next)
        if (duration.isNegative) {
            throw IllegalStateException("Trying to sleep for a negative amount of time: $duration between $now and $next")
        }
        logger.info("Task $name: Next is at $next, sleeping for $duration")
        delay(duration.toMillis())
    }

    fun route(routing: Routing) {
        routing.route("/tasks") {
            get {
                call.respond(taskExecutions.get().mapValues {
                    mapOf(
                        "name" to it.value.name,
                        "count" to it.value.count,
                        "last" to it.value.last?.toEpochSecond(),
                        "next" to it.value.next?.toEpochSecond()
                    )
                })
            }
        }
    }

}