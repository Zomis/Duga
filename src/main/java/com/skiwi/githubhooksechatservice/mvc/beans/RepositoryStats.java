package com.skiwi.githubhooksechatservice.mvc.beans;

import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public class RepositoryStats {
	
	private final AtomicInteger issuesOpened = new AtomicInteger();
	private final AtomicInteger issuesClosed = new AtomicInteger();
	private final AtomicInteger commits = new AtomicInteger();
	private final String url;
	private final String name;
	
	public RepositoryStats(String url, String name) {
		this.url = url;
		this.name = name;
	}
	
	public int getCommits() {
		return commits.get();
	}
	
	public int getIssuesClosed() {
		return issuesClosed.get();
	}
	
	public int getIssuesOpened() {
		return issuesOpened.get();
	}
	
	public void addIssueClosed() {
		issuesClosed.incrementAndGet();
	}

	public void addIssueOpened() {
		issuesOpened.incrementAndGet();
	}

	public void addCommit() {
		commits.incrementAndGet();
	}
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}

}
