package com.skiwi.githubhooksechatservice.dao;


public interface ConfigDAO {
	
	String getConfig(String key, String defaultValue);
	void setConfig(String key, String value);

}
