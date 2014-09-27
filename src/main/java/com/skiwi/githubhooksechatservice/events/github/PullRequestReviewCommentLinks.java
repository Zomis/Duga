
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class PullRequestReviewCommentLinks {
	@JsonProperty
	private Link self;
	
	@JsonProperty
	private Link html;
	
	@JsonProperty("pull_request")
	private Link pullRequest;

	public Link getSelf() {
		return self;
	}

	public Link getHtml() {
		return html;
	}

	public Link getPullRequest() {
		return pullRequest;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + Objects.hashCode(this.self);
		hash = 19 * hash + Objects.hashCode(this.html);
		hash = 19 * hash + Objects.hashCode(this.pullRequest);
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
		final PullRequestReviewCommentLinks other = (PullRequestReviewCommentLinks)obj;
		if (!Objects.equals(this.self, other.self)) {
			return false;
		}
		if (!Objects.equals(this.html, other.html)) {
			return false;
		}
		if (!Objects.equals(this.pullRequest, other.pullRequest)) {
			return false;
		}
		return true;
	}
}
