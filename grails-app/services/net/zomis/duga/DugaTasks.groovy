package net.zomis.duga

import grails.transaction.Transactional
import net.zomis.duga.tasks.CommentsScanTask
import net.zomis.duga.tasks.GithubTask
import net.zomis.duga.tasks.MessageTask
import net.zomis.duga.tasks.StatisticTask
import net.zomis.duga.tasks.UnansweredTask
import net.zomis.duga.tasks.UserRepDiffTask
import net.zomis.duga.tasks.qscan.QuestionScanTask
import org.apache.log4j.Logger
import org.codehaus.groovy.control.CompilerConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger

import java.util.concurrent.ScheduledFuture

@Transactional(readOnly = true)
class DugaTasks {

    private static final Logger logger = Logger.getLogger(DugaTasks)

    private final List<ScheduledFuture<?>> tasks = new ArrayList<>()
    private final List<TaskData> taskData = new ArrayList<TaskData>();

    @Autowired TaskScheduler scheduler

    @Autowired DugaFileConfig dugaConfig

    def initOnce() {
        reloadAll()
    }

    List<TaskData> reloadAll() {
        List<TaskData> allTasks = dugaConfig.getTasks()
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
        return allTasks
    }


    @Autowired private DugaBotService chatBot;
    @Autowired private GithubBean githubBean;
    @Autowired private StackExchangeAPI stackAPI;
    @Autowired private HookStringification stringification;
    @Autowired private DugaMachineLearning learning;

    private class TaskRunner implements Runnable {

        private final Object data;
        private final Runnable task;

        public TaskRunner(Object data, Runnable runnable) {
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
            case "dailyStats":
                return new StatisticTask(chatBot, taskInfo[1])
            case "questionScan":
                return new QuestionScanTask(stackAPI, githubBean,
                        stringification, chatBot,
                        taskInfo[1], taskInfo[2], taskInfo[3])
            case "github":
                return new GithubTask(githubBean, stringification, chatBot)
            case "comments":
                return new CommentsScanTask(stackAPI, chatBot, learning)
            case "mess":
                return new MessageTask(chatBot, taskInfo[1], taskInfo[2])
            case "ratingdiff":
                return new UserRepDiffTask(stackAPI, taskInfo[1], chatBot, taskInfo[2], taskInfo[3])
            case "unanswered":
                return new UnansweredTask(stackAPI, taskInfo[1], chatBot, taskInfo[2], taskInfo[3])
            default:
                return { println "Unknown task: $taskData" }
        }
    }

    public synchronized List<TaskData> getTasks() {
        return new ArrayList<>(taskData);
    }

    public void fromGroovyDSL(String groovyCode) {
        def cc = new CompilerConfiguration()
        cc.setScriptBaseClass(TasksDelegate.class.name)
        GroovyShell sh = new GroovyShell(cc);
        println "parsing script"
        TasksDelegate script = (TasksDelegate) sh.parse(groovyCode, 'InitTasks.groovy')
        println "setting script outer to $this on $script"
        script.setOuter(this);
        println "outer is now $script.outer called from $this on $script, running script"
        script.run()
        println "script finished"
        for (CronRunnable cronRunnable : script.data) {
            def runner = new TaskRunner(cronRunnable, cronRunnable.runnable)
            ScheduledFuture<?> future = scheduler.schedule(runner, cronRunnable.trigger)
            tasks.add(future)
            System.out.println("Added task: $cronRunnable with cron $cronRunnable.trigger")
        }
    }

    public static class CronRunnable {
        CronTrigger trigger;
        Runnable runnable;
    }

    public static abstract class TasksDelegate extends Script {

        private final List<CronRunnable> data = [];
        CronTrigger trigger;
        private DugaTasks outer;

        public List<CronRunnable> getData() {
            return this.@data
        }

        public void tasks(Closure closure) {
            println "tasks run closure with " + this.@outer
            closure.setDelegate(this)
            closure.run()
        }

        public void cron(String cron, Closure closure) {
            println "cron run closure with " + this.@outer
            trigger = new CronTrigger(cron, TimeZone.getTimeZone("UTC"))
            closure.delegate = this
            closure.run()
        }

        public void message(String room, String message) {
            println "get outer returns: " + getOuter()
            createTask(new MessageTask(this.@outer.chatBot, room, message))
        }

        public void repdiff(String room, String site, int user1, int user2) {
            createTask(new UserRepDiffTask(this.@outer.stackAPI, room, this.@outer.chatBot, "$user1,$user2", site))
        }

        private void createTask(Runnable runnable) {
            CronRunnable cronRunnable = new CronRunnable()
            cronRunnable.trigger = trigger;
            cronRunnable.runnable = runnable;
            data.add(cronRunnable)
        }

        public void setOuter(DugaTasks outer) {
            println "Set outer to $outer on $this"
            this.@outer = outer
        }

        public DugaTasks getOuter() {
            println "get outer for $this"
            this.@outer
        }

    }

}
