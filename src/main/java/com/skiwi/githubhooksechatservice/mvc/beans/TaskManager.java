package com.skiwi.githubhooksechatservice.mvc.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.skiwi.githubhooksechatservice.model.TaskData;
import com.skiwi.githubhooksechatservice.service.TaskService;

public class TaskManager {
	
	@Autowired
	private TaskScheduler scheduler;
	
	@Autowired
	private TaskService taskService;
	
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
			ScheduledFuture<?> future = scheduler.schedule(taskToRunnable(data), new CronTrigger(data.getCron()));
			tasks.add(future);
		}
	}

	private Runnable taskToRunnable(TaskData data) {
		return () -> System.out.println(data.getTaskValue());
	}

	public synchronized List<TaskData> getTasks() {
		return new ArrayList<>(taskData);
	}

	public TaskData add(String cron, String task) {
		return taskService.add(cron, task);
	}
	
}
