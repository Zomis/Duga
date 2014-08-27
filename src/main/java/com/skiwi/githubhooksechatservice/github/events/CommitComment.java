
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class CommitComment {
	@JsonProperty
	private String url;
	
	@JsonProperty("html_url")
	private String htmlUrl;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private User user;
	
	@JsonProperty
	private Long position;
	
	@JsonProperty
	private Long line;
	
	@JsonProperty
	private String path;
	
	@JsonProperty("commit_id")
	private String commitId;
	
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

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Long getPosition() {
		return position;
	}

	public Long getLine() {
		return line;
	}

	public String getPath() {
		return path;
	}

	public String getCommitId() {
		return commitId;
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
