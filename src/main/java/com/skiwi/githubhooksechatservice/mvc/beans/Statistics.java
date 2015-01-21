package com.skiwi.githubhooksechatservice.mvc.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.skiwi.githubhooksechatservice.events.github.IssuesEvent;
import com.skiwi.githubhooksechatservice.events.github.classes.GithubRepository;
import com.skiwi.githubhooksechatservice.events.github.classes.LegacyCommit;
import com.skiwi.githubhooksechatservice.events.github.classes.LegacyRepository;
import com.skiwi.githubhooksechatservice.events.travis.Repository;

public class Statistics {

	private final Map<String, RepositoryStats> repoStats;
	
	public Statistics() {
		repoStats = new ConcurrentHashMap<>();
	}
	
	// TODO: Doesn't feel right to synchronize all, use explicit read/write lock? the get + reset can have an add between them

	public synchronized void add(IssuesEvent issuesEvent, boolean increased) {
		RepositoryStats stats = putIfAbsent(issuesEvent.getRepository());
		if (increased) {
			stats.addIssueOpened();
		}
		else {
			stats.addIssueClosed();
		}
	}

	private RepositoryStats putIfAbsent(GithubRepository repository) {
		repoStats.putIfAbsent(repository.getHtmlUrl(), new RepositoryStats(repository.getHtmlUrl(), repository.getFullName()));
		return repoStats.get(repository.getHtmlUrl());
	}

	public synchronized void fixRepositoryURL(Repository repository) {
		for (RepositoryStats githubRepo : repoStats.values()) {
			if (repository.getFullNameGithubStyle().equals(githubRepo.getName())) {
				repository.setUrl(githubRepo.getUrl());
			}
		}
	}
	
	public synchronized void add(LegacyRepository repository, LegacyCommit commit) {
		RepositoryStats stats = putIfAbsent(repository);
		stats.addCommit();
	}
	
	public synchronized void reset() {
		repoStats.clear();
	}
	
	public synchronized Map<String, RepositoryStats> getRepoStats() {
		return new HashMap<>(repoStats);
	}

	public void informAboutURL(GithubRepository repository) {
		putIfAbsent(repository);
	}
	
}
