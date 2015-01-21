package com.skiwi.githubhooksechatservice.service;


public interface ConfigService {
	
	String getConfig(String key, String defaultValue);
	void setConfig(String key, String value);

}
