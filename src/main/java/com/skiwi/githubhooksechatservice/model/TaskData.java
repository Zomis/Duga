package com.skiwi.githubhooksechatservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="duga_task")
public class TaskData {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String cron;
	
	private String taskValue;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCron() {
		return cron;
	}
	
	public void setCron(String cron) {
		this.cron = cron;
	}
	
	public String getTaskValue() {
		return taskValue;
	}
	
	public void setTaskValue(String taskValue) {
		this.taskValue = taskValue;
	}

}
