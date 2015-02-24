
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.skiwi.githubhooksechatservice.events.github.classes.PullRequest;
import com.skiwi.githubhooksechatservice.events.github.classes.PullRequestReviewComment;

/**
 *
 * @author Frank van Heeswijk
 */
@JsonTypeInfo(use = Id.NAME, defaultImpl = PullRequestReviewCommentEvent.class)
public final class PullRequestReviewCommentEvent extends GithubEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private PullRequestReviewComment comment;
	
	@JsonProperty("pull_request")
	private PullRequest pullRequest;
	
	public String getAction() {
		return action;
	}

	public PullRequestReviewComment getComment() {
		return comment;
	}

	public PullRequest getPullRequest() {
		return pullRequest;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.action);
		hash = 59 * hash + Objects.hashCode(this.comment);
		hash = 59 * hash + Objects.hashCode(this.pullRequest);
		hash = 59 * hash + Objects.hashCode(this.repository);
		hash = 59 * hash + Objects.hashCode(this.organization);
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
		if (!Objects.equals(this.organization, other.organization)) {
			return false;
		}
		if (!Objects.equals(this.sender, other.sender)) {
			return false;
		}
		return true;
	}
	
	public void setPayload(PullRequestReviewCommentEvent event) {
		this.action = event.action;
		this.comment = event.comment;
		this.pullRequest = event.pullRequest;
	}
	
}
