package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skiwi.githubhooksechatservice.dao.TaskDAO;
import com.skiwi.githubhooksechatservice.model.TaskData;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
	
	@Autowired
	private TaskDAO taskDAO;

	@Override
	public List<TaskData> getTasks() {
		return taskDAO.getTasks();
	}

	@Override
	public TaskData add(String cron, String task) {
		return taskDAO.add(cron, task);
	}

}
