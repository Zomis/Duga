package com.skiwi.githubhooksechatservice.service;

public interface RuntimeLogService {
	
	void log(String tag, String message);
	void log(String message);
	
}
