package com.skiwi.githubhooksechatservice.dao;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.DailyInfo;


public interface DailyInfoDAO {
	
	DailyInfo add(String name, String url, int commits, int opened, int closed,
			int additions, int deletions);
	List<DailyInfo> getAndReset();
	List<DailyInfo> get();
	String getUrl(String fullNameGithubStyle);

}
