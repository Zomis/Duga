
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.skiwi.githubhooksechatservice.events.github.classes.Issue;
import com.skiwi.githubhooksechatservice.events.github.classes.IssueComment;
import com.skiwi.githubhooksechatservice.events.github.classes.User;

/**
 *
 * @author Frank van Heeswijk
 */
@JsonTypeInfo(use = Id.NAME, defaultImpl = IssueCommentEvent.class)
public final class IssueCommentEvent extends GithubEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private Issue issue;
	
	@JsonProperty
	private IssueComment comment;
	
	@JsonProperty
	private User sender;

	public String getAction() {
		return action;
	}

	public Issue getIssue() {
		return issue;
	}

	public IssueComment getComment() {
		return comment;
	}

	public User getSender() {
		return sender;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.action);
		hash = 97 * hash + Objects.hashCode(this.issue);
		hash = 97 * hash + Objects.hashCode(this.comment);
		hash = 97 * hash + Objects.hashCode(this.repository);
		hash = 97 * hash + Objects.hashCode(this.organization);
		hash = 97 * hash + Objects.hashCode(this.sender);
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
		final IssueCommentEvent other = (IssueCommentEvent)obj;
		if (!Objects.equals(this.action, other.action)) {
			return false;
		}
		if (!Objects.equals(this.issue, other.issue)) {
			return false;
		}
		if (!Objects.equals(this.comment, other.comment)) {
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
	
	public void setPayload(IssueCommentEvent event) {
		this.action = event.action;
		this.comment = event.comment;
		this.issue = event.issue;
	}
	
}
