
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class PullRequestLinks {
	@JsonProperty
	private Link self;
	
	@JsonProperty
	private Link html;
	
	@JsonProperty
	private Link issue;
	
	@JsonProperty
	private Link comments;
	
	@JsonProperty("review_comments")
	private Link reviewComments;
	
	@JsonProperty("review_comment")
	private Link reviewComment;
	
	@JsonProperty
	private Link commits;
	
	@JsonProperty
	private Link statuses;

	public Link getSelf() {
		return self;
	}

	public Link getHtml() {
		return html;
	}

	public Link getIssue() {
		return issue;
	}

	public Link getComments() {
		return comments;
	}

	public Link getReviewComments() {
		return reviewComments;
	}

	public Link getReviewComment() {
		return reviewComment;
	}

	public Link getCommits() {
		return commits;
	}

	public Link getStatuses() {
		return statuses;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + Objects.hashCode(this.self);
		hash = 37 * hash + Objects.hashCode(this.html);
		hash = 37 * hash + Objects.hashCode(this.issue);
		hash = 37 * hash + Objects.hashCode(this.comments);
		hash = 37 * hash + Objects.hashCode(this.reviewComments);
		hash = 37 * hash + Objects.hashCode(this.reviewComment);
		hash = 37 * hash + Objects.hashCode(this.commits);
		hash = 37 * hash + Objects.hashCode(this.statuses);
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
		final PullRequestLinks other = (PullRequestLinks)obj;
		if (!Objects.equals(this.self, other.self)) {
			return false;
		}
		if (!Objects.equals(this.html, other.html)) {
			return false;
		}
		if (!Objects.equals(this.issue, other.issue)) {
			return false;
		}
		if (!Objects.equals(this.comments, other.comments)) {
			return false;
		}
		if (!Objects.equals(this.reviewComments, other.reviewComments)) {
			return false;
		}
		if (!Objects.equals(this.reviewComment, other.reviewComment)) {
			return false;
		}
		if (!Objects.equals(this.commits, other.commits)) {
			return false;
		}
		if (!Objects.equals(this.statuses, other.statuses)) {
			return false;
		}
		return true;
	}
}
