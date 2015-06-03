package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.TaskData;

public interface TaskService {
	
	List<TaskData> getTasks();

	TaskData add(String cron, String task);

}
