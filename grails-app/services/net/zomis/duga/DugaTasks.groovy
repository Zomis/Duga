package net.zomis.duga

import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger

import java.util.concurrent.ScheduledFuture

@Transactional(readOnly = true)
class DugaTasks {

    private final List<ScheduledFuture<?>> tasks = new ArrayList<>()

    @Autowired TaskScheduler scheduler

    def reloadAll() {
        List<TaskData> allTasks = TaskData.list()
        println 'Reloading tasks, contains ' + allTasks
        tasks.forEach({ScheduledFuture<?> task -> task.cancel(false) })
        for (TaskData task : allTasks) {
            Runnable run = createTask(task.taskValue)
            scheduler.schedule(run, new CronTrigger(task.cron, TimeZone.getTimeZone("UTC")))
        }
    }

    static Runnable createTask(String s) {
        return {
            println 'Task: ' + s
        }
    }
}
