package com.skiwi.githubhooksechatservice.service;

import java.util.List;

import com.skiwi.githubhooksechatservice.events.github.classes.GithubRepository;
import com.skiwi.githubhooksechatservice.model.DailyInfo;


public interface DailyService {
	
	DailyInfo addCommits(GithubRepository repository, int commits, int additions, int deletions);
	DailyInfo addIssues(GithubRepository repository, int opened, int closed, int comments);
	
	List<DailyInfo> getAndReset();
	List<DailyInfo> get();
	String getUrl(String fullNameGithubStyle);

}
