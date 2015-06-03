package com.skiwi.githubhooksechatservice.mvc.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.model.TaskData;
import com.skiwi.githubhooksechatservice.mvc.beans.tasks.CommentsScanTask;
import com.skiwi.githubhooksechatservice.mvc.beans.tasks.GithubTask;
import com.skiwi.githubhooksechatservice.mvc.beans.tasks.MessageTask;
import com.skiwi.githubhooksechatservice.mvc.beans.tasks.StatisticTask;
import com.skiwi.githubhooksechatservice.mvc.beans.tasks.UnansweredTask;
import com.skiwi.githubhooksechatservice.mvc.beans.tasks.UserRepDiffTask;
import com.skiwi.githubhooksechatservice.mvc.controllers.GithubHookController;
import com.skiwi.githubhooksechatservice.service.ConfigService;
import com.skiwi.githubhooksechatservice.service.DailyService;
import com.skiwi.githubhooksechatservice.service.GithubService;
import com.skiwi.githubhooksechatservice.service.TaskService;

public class TaskManager {
	private static final Logger logger = LogManager.getLogger(TaskManager.class);
	
	@Autowired
	private TaskScheduler scheduler;
	
	@Autowired
	private TaskService taskService;
	
    private final GithubEventFilter eventFilter = new GithubEventFilter();
    
    @Autowired private ChatBot chatBot;
    @Autowired private DailyService dailyService;
    @Autowired private ConfigService configService;
    @Autowired private GithubService githubService;
    @Autowired private GithubBean githubBean;
    @Autowired private StackExchangeAPIBean stackAPI;
    @Autowired private GithubHookController controller;
	
	private final List<ScheduledFuture<?>> tasks = new ArrayList<ScheduledFuture<?>>();
	private final List<TaskData> taskData = new ArrayList<TaskData>();
	
	@PostConstruct
	public void startup() {
		reload();
	}
	
	public synchronized void reload() {
		tasks.forEach(f -> f.cancel(false));
		tasks.clear();
		taskData.clear();
		taskData.addAll(taskService.getTasks());
		for (TaskData data : taskData) {
			Runnable runnable = taskToRunnable(data);
			ScheduledFuture<?> future = scheduler.schedule(new TaskRunner(data, runnable), new CronTrigger(data.getCron()));
			tasks.add(future);
			System.out.println("Added task: " + runnable);
		}
	}
	
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

	private Runnable taskToRunnable(TaskData data) {
		String[] taskInfo = data.getTaskValue().split(";");
		switch (taskInfo[0]) {
			case "dailyStats":
				return new StatisticTask(dailyService, configService, chatBot);
			case "github":
				return new GithubTask(githubService, githubBean, eventFilter, controller);
			case "comments":
				return new CommentsScanTask(stackAPI, chatBot);
			case "mess":
				return new MessageTask(chatBot, taskInfo[1], taskInfo[2]);
			case "ratingdiff":
				return new UserRepDiffTask(stackAPI, taskInfo[1], chatBot, taskInfo[2], taskInfo[3]);
			case "unanswered":
				return new UnansweredTask(stackAPI, taskInfo[1], chatBot, taskInfo[2], taskInfo[3]);
			default:
				return () -> System.out.println("Unknown task: " + data.getTaskValue());
		}
	}

	public synchronized List<TaskData> getTasks() {
		return new ArrayList<>(taskData);
	}

	public TaskData add(String cron, String task) {
		return taskService.add(cron, task);
	}
	
}
