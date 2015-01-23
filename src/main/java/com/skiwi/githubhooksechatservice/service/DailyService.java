package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import com.skiwi.githubhooksechatservice.events.github.classes.GithubRepository;
import com.skiwi.githubhooksechatservice.model.DailyInfo;


public interface DailyService {
	
	DailyInfo add(GithubRepository repository, int commits, int opened, int closed,
			int additions, int deletions);
	List<DailyInfo> getAndReset();
	List<DailyInfo> get();
	String getUrl(String fullNameGithubStyle);

}
