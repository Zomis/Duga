package com.skiwi.githubhooksechatservice.mvc.beans;

import org.springframework.beans.factory.annotation.Autowired;

import com.skiwi.githubhooksechatservice.events.github.IssueCommentEvent;
import com.skiwi.githubhooksechatservice.events.github.IssuesEvent;
import com.skiwi.githubhooksechatservice.events.github.classes.GithubRepository;
import com.skiwi.githubhooksechatservice.events.github.classes.LegacyCommit;
import com.skiwi.githubhooksechatservice.events.github.classes.LegacyRepository;
import com.skiwi.githubhooksechatservice.events.travis.Repository;
import com.skiwi.githubhooksechatservice.service.DailyService;

public class Statistics {

	@Autowired
	private DailyService service;
	
	public void add(IssuesEvent issuesEvent, boolean increased) {
		if (increased) {
			service.addIssues(issuesEvent.getRepository(), 1, 0, 0);
		}
		else {
			service.addIssues(issuesEvent.getRepository(), 0, 1, 0);
		}
	}

	public void add(IssueCommentEvent issueCommentEvent) {
		service.addIssues(issueCommentEvent.getRepository(), 0, 0, 1);
	}

	public void fixRepositoryURL(Repository repository) {
		String url = service.getUrl(repository.getFullNameGithubStyle());
		if (url != null) {
			repository.setUrl(url);
		}
	}
	
	public void add(LegacyRepository repository, LegacyCommit commit) {
		final int additions = 0;
		final int deletions = 0;
		service.addCommits(repository, 1, additions, deletions);
	}
	
	public void informAboutURL(GithubRepository repository) {
		service.addCommits(repository, 0, 0, 0);
	}
	
}
