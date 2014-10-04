
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class PullRequestReviewComment extends AnySetterJSONObject {
	@JsonProperty
	private String url;
	
	@JsonProperty
	private long id;
	
	@JsonProperty("diff_hunk")
	private String diffHunk;
	
	@JsonProperty
	private String path;
	
	@JsonProperty
	private long position;
	
	@JsonProperty("original_position")
	private long originalPosition;
	
	@JsonProperty("commit_id")
	private String commitId;
	
	@JsonProperty("original_commit_id")
	private String originalCommitId;
	
	@JsonProperty
	private User user;
	
	@JsonProperty
	private String body;
	
	@JsonProperty("created_at")
	private String createdAt;
	
	@JsonProperty("updated_at")
	private String updatedAt;
	
	@JsonProperty("html_url")
	private String htmlUrl;
	
	@JsonProperty("pull_request_url")
	private String pullRequestUrl;
	
	@JsonProperty("_links")
	private PullRequestReviewCommentLinks links;

	public String getUrl() {
		return url;
	}

	public long getId() {
		return id;
	}

	public String getDiffHunk() {
		return diffHunk;
	}

	public String getPath() {
		return path;
	}

	public long getPosition() {
		return position;
	}

	public long getOriginalPosition() {
		return originalPosition;
	}

	public String getCommitId() {
		return commitId;
	}

	public String getOriginalCommitId() {
		return originalCommitId;
	}

	public User getUser() {
		return user;
	}

	public String getBody() {
		return body;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public String getPullRequestUrl() {
		return pullRequestUrl;
	}

	public PullRequestReviewCommentLinks getLinks() {
		return links;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.url);
		hash = 67 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 67 * hash + Objects.hashCode(this.diffHunk);
		hash = 67 * hash + Objects.hashCode(this.path);
		hash = 67 * hash + (int)(this.position ^ (this.position >>> 32));
		hash = 67 * hash + (int)(this.originalPosition ^ (this.originalPosition >>> 32));
		hash = 67 * hash + Objects.hashCode(this.commitId);
		hash = 67 * hash + Objects.hashCode(this.originalCommitId);
		hash = 67 * hash + Objects.hashCode(this.user);
		hash = 67 * hash + Objects.hashCode(this.body);
		hash = 67 * hash + Objects.hashCode(this.createdAt);
		hash = 67 * hash + Objects.hashCode(this.updatedAt);
		hash = 67 * hash + Objects.hashCode(this.htmlUrl);
		hash = 67 * hash + Objects.hashCode(this.pullRequestUrl);
		hash = 67 * hash + Objects.hashCode(this.links);
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
		final PullRequestReviewComment other = (PullRequestReviewComment)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.diffHunk, other.diffHunk)) {
			return false;
		}
		if (!Objects.equals(this.path, other.path)) {
			return false;
		}
		if (this.position != other.position) {
			return false;
		}
		if (this.originalPosition != other.originalPosition) {
			return false;
		}
		if (!Objects.equals(this.commitId, other.commitId)) {
			return false;
		}
		if (!Objects.equals(this.originalCommitId, other.originalCommitId)) {
			return false;
		}
		if (!Objects.equals(this.user, other.user)) {
			return false;
		}
		if (!Objects.equals(this.body, other.body)) {
			return false;
		}
		if (!Objects.equals(this.createdAt, other.createdAt)) {
			return false;
		}
		if (!Objects.equals(this.updatedAt, other.updatedAt)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (!Objects.equals(this.pullRequestUrl, other.pullRequestUrl)) {
			return false;
		}
		if (!Objects.equals(this.links, other.links)) {
			return false;
		}
		return true;
	}
}
