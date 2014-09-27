
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitComment {
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

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(this.url);
		hash = 59 * hash + Objects.hashCode(this.htmlUrl);
		hash = 59 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 59 * hash + Objects.hashCode(this.user);
		hash = 59 * hash + Objects.hashCode(this.position);
		hash = 59 * hash + Objects.hashCode(this.line);
		hash = 59 * hash + Objects.hashCode(this.path);
		hash = 59 * hash + Objects.hashCode(this.commitId);
		hash = 59 * hash + Objects.hashCode(this.createdAt);
		hash = 59 * hash + Objects.hashCode(this.updatedAt);
		hash = 59 * hash + Objects.hashCode(this.body);
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
		final CommitComment other = (CommitComment)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.user, other.user)) {
			return false;
		}
		if (!Objects.equals(this.position, other.position)) {
			return false;
		}
		if (!Objects.equals(this.line, other.line)) {
			return false;
		}
		if (!Objects.equals(this.path, other.path)) {
			return false;
		}
		if (!Objects.equals(this.commitId, other.commitId)) {
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
