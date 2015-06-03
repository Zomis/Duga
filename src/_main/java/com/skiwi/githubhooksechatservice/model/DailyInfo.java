package com.skiwi.githubhooksechatservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="daily_info")
public class DailyInfo {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String name;
	
	private String url;
	
	private String comment = "";
	
	private Integer commits = 0;
	
	private Integer issuesOpened = 0;
	
	private Integer issuesClosed = 0;
	
	private Integer additions = 0;
	
	private Integer deletions = 0;
	
	private Integer comments = 0;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCommits() {
		return commits;
	}
	
	public Integer getIssuesClosed() {
		return issuesClosed;
	}
	
	public Integer getIssuesOpened() {
		return issuesOpened;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setCommits(Integer commits) {
		this.commits = commits;
	}
	
	public void setIssuesClosed(Integer issuesClosed) {
		this.issuesClosed = issuesClosed;
	}
	
	public void setIssuesOpened(Integer issuesOpened) {
		this.issuesOpened = issuesOpened;
	}
	
	public Integer getAdditions() {
		return additions;
	}
	
	public Integer getDeletions() {
		return deletions;
	}
	
	public void setAdditions(Integer additions) {
		this.additions = additions;
	}
	
	public void setDeletions(Integer deletions) {
		this.deletions = deletions;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getComments() {
		return comments;
	}
	
	public void setComments(Integer comments) {
		this.comments = comments;
	}

	public void addIssues(int opened, int closed, int comments) {
		this.issuesOpened += opened;
		this.issuesClosed += closed;
		this.comments += comments;
	}
	
	public void addCommits(int commits, int additions, int deletions) {
		this.commits += commits;
		this.additions += additions;
		this.deletions += deletions;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}
	
}
