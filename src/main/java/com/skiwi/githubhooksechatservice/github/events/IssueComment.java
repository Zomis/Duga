
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class IssueComment {
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

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + Objects.hashCode(this.url);
		hash = 29 * hash + Objects.hashCode(this.htmlUrl);
		hash = 29 * hash + Objects.hashCode(this.issueUrl);
		hash = 29 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 29 * hash + Objects.hashCode(this.user);
		hash = 29 * hash + Objects.hashCode(this.createdAt);
		hash = 29 * hash + Objects.hashCode(this.updatedAt);
		hash = 29 * hash + Objects.hashCode(this.body);
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final IssueComment other = (IssueComment)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (!Objects.equals(this.issueUrl, other.issueUrl)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.user, other.user)) {
			return false;
		}
		if (!Objects.equals(this.createdAt, other.createdAt)) {
			return false;
		}
		if (!Objects.equals(this.updatedAt, other.updatedAt)) {
			return false;
		}
		if (!Objects.equals(this.body, other.body)) {
			return false;
		}
		return true;
	}
}
