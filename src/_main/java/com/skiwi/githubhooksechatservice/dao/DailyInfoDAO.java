package com.skiwi.githubhooksechatservice.dao;

import java.util.List;

import com.skiwi.githubhooksechatservice.model.DailyInfo;


public interface DailyInfoDAO {
	
	DailyInfo addIssues(String name, String url, int opened, int closed, int comments);
	DailyInfo addCommits(String name, String url, int commits, int additions, int deletions);
	List<DailyInfo> getAndReset();
	List<DailyInfo> get();
	String getUrl(String fullNameGithubStyle);

}
