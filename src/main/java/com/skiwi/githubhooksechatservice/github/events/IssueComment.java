
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class IssueComment {
	@JsonProperty
	private String url;
	
	@JsonProperty("html_url")
	private String htmlUrl;
	
	@JsonProperty("issue_url")
	private String issueUrl;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private User user;
	
	@JsonProperty("created_at")
	private String createdAt;
	
	@JsonProperty("updated_at")
	private String updatedAt;
	
	@JsonProperty
	private String body;

	public String getUrl() {
		return url;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public String getIssueUrl() {
		return issueUrl;
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getBody() {
		return body;
	}
}
