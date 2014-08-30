
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class PullRequestReviewCommentEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private PullRequestReviewComment comment;
	
	@JsonProperty("pull_request")
	private PullRequest pullRequest;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty
	private User sender;

	public String getAction() {
		return action;
	}

	public PullRequestReviewComment getComment() {
		return comment;
	}

	public PullRequest getPullRequest() {
		return pullRequest;
	}

	public Repository getRepository() {
		return repository;
	}

	public User getSender() {
		return sender;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.action);
		hash = 59 * hash + Objects.hashCode(this.comment);
		hash = 59 * hash + Objects.hashCode(this.pullRequest);
		hash = 59 * hash + Objects.hashCode(this.repository);
		hash = 59 * hash + Objects.hashCode(this.sender);
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
		final PullRequestReviewCommentEvent other = (PullRequestReviewCommentEvent)obj;
		if (!Objects.equals(this.action, other.action)) {
			return false;
		}
		if (!Objects.equals(this.comment, other.comment)) {
			return false;
		}
		if (!Objects.equals(this.pullRequest, other.pullRequest)) {
			return false;
		}
		if (!Objects.equals(this.repository, other.repository)) {
			return false;
		}
		if (!Objects.equals(this.sender, other.sender)) {
			return false;
		}
		return true;
	}
}
