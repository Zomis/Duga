package net.zomis.duga

import grails.transaction.Transactional
import net.zomis.duga.tasks.CommentsScanTask
import net.zomis.duga.tasks.ListenTask
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger

import java.util.concurrent.ScheduledFuture

@Transactional(readOnly = true)
class DugaTasks {

    private static final Logger logger = Logger.getLogger(DugaTasks)

    private final List<ScheduledFuture<?>> tasks = new ArrayList<>()
    private final List<TaskData> taskData = new ArrayList<TaskData>();
    private ChatCommands commands

    @Autowired TaskScheduler scheduler

    def initOnce() {
        assert !commands
        commands = new ChatCommands(this, chatBot)
        reloadAll()
    }

    def reloadAll() {
        List<TaskData> allTasks = TaskData.list()
        println 'Reloading tasks, contains ' + allTasks
        tasks.forEach({ScheduledFuture<?> task -> task.cancel(false) })
        tasks.clear()
        taskData.clear()
        taskData.addAll(allTasks)
        for (TaskData task : allTasks) {
            Runnable run = createTask(task.taskValue)
            ScheduledFuture<?> future = scheduler.schedule(new TaskRunner(task, run), new CronTrigger(task.cronStr, TimeZone.getTimeZone("UTC")))
            tasks.add(future)
            System.out.println("Added task: $task.taskValue - $run")
        }
        scheduler.scheduleWithFixedDelay(new ListenTask(chatBot, '20298', commands), 3000) //new CronTrigger('*/3 * * * * *')
    }


//    private final GithubEventFilter eventFilter = new GithubEventFilter();
    @Autowired private DugaBot chatBot;
//    @Autowired private GithubBean githubBean;
    @Autowired private StackExchangeAPI stackAPI;
    @Autowired private GithubHookController controller;

    private class TaskRunner implements Runnable {

        private final TaskData data;
        private final Runnable task;

        public TaskRunner(TaskData data, Runnable runnable) {
            this.data = data;
            this.task = runnable;
        }

        @Override
        public void run() {
            try {
                logger.info("Running task " + data);
                task.run();
                logger.info("Finished task " + data);
            } catch (Exception ex) {
                logger.error("Error running " + data, ex);
            }
        }
    }

    Runnable createTask(String taskData) {
        String[] taskInfo = taskData.split(";")
        switch (taskInfo[0]) {
/*            case "dailyStats":
                return new StatisticTask(dailyService, configService, chatBot)
            case "github":
                return new GithubTask(githubService, githubBean, eventFilter, controller)*/
            case "comments":
                return new CommentsScanTask(stackAPI, chatBot)
/*            case "mess":
                return new MessageTask(chatBot, taskInfo[1], taskInfo[2])
            case "ratingdiff":
                return new UserRepDiffTask(stackAPI, taskInfo[1], chatBot, taskInfo[2], taskInfo[3])
            case "unanswered":
                return new UnansweredTask(stackAPI, taskInfo[1], chatBot, taskInfo[2], taskInfo[3])*/
            default:
                return { println "Unknown task: $taskData" }
        }
    }

    public synchronized List<TaskData> getTasks() {
        return new ArrayList<>(taskData);
    }

    public TaskData add(String cron, String task) {
        return taskService.add(cron, task);
    }

}
