
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class Milestone {
	@JsonProperty
	private String url;
	
	@JsonProperty("labels_url")
	private String labelsUrl;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private long number;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	private User creator;
	
	@JsonProperty("open_issues")
	private long openIssues;
	
	@JsonProperty("closed_issues")
	private long closedIssues;
	
	@JsonProperty
	private String state;
	
	@JsonProperty("created_at")
	private String createdAt;
	
	@JsonProperty("updated_at")
	private String updatedAt;
	
	@JsonProperty("due_on")
	private String dueOn;
	

	public String getUrl() {
		return url;
	}

	public String getLabelsUrl() {
		return labelsUrl;
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

	public String getDescription() {
		return description;
	}

	public User getCreator() {
		return creator;
	}

	public long getOpenIssues() {
		return openIssues;
	}

	public long getClosedIssues() {
		return closedIssues;
	}

	public String getState() {
		return state;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getDueOn() {
		return dueOn;
	}
}
