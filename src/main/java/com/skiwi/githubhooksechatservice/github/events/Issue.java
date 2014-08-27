
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class Issue {
	@JsonProperty
	private String url;
	
	@JsonProperty("labels_url")
	private String labelsUrl;
	
	@JsonProperty("comments_url")
	private String commentsUrl;
	
	@JsonProperty("events_url")
	private String eventsUrl;
	
	@JsonProperty("html_url")
	private String htmlUrl;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private long number;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private User user;
	
	@JsonProperty
	private IssueLabel[] labels;
	
	@JsonProperty
	private String state;
	
	@JsonProperty
	private boolean locked;
	
	@JsonProperty
	private User assignee;
	
	@JsonProperty
	private Milestone milestone;
	
	@JsonProperty
	private long comments;
	
	@JsonProperty("created_at")
	private String createdAt;
	
	@JsonProperty("updated_at")
	private String updatedAt;
	
	@JsonProperty("closed_at")
	private String closedAt;
	
	@JsonProperty
	private String body;

	public String getUrl() {
		return url;
	}

	public String getLabelsUrl() {
		return labelsUrl;
	}

	public String getCommentsUrl() {
		return commentsUrl;
	}

	public String getEventsUrl() {
		return eventsUrl;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public long getId() {
		return id;
	}

	public long getNumber() {
		return number;
	}

	public String getTitle() {
		return title;
	}

	public User getUser() {
		return user;
	}

	public List<IssueLabel> getLabels() {
		return Arrays.asList(labels);
	}

	public String getState() {
		return state;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public User getAssignee() {
		return assignee;
	}

	public Milestone getMilestone() {
		return milestone;
	}

	public long getComments() {
		return comments;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getClosedAt() {
		return closedAt;
	}

	public String getBody() {
		return body;
	}
}
