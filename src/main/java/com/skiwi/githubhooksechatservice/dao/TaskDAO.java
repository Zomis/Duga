package com.skiwi.githubhooksechatservice.dao;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.TaskData;


public interface TaskDAO {
	
	List<TaskData> getTasks();

}
