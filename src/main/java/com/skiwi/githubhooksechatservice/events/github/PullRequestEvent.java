
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.skiwi.githubhooksechatservice.events.github.classes.Label;
import com.skiwi.githubhooksechatservice.events.github.classes.PullRequest;
import com.skiwi.githubhooksechatservice.events.github.classes.User;

/**
 *
 * @author Frank van Heeswijk
 */
@JsonTypeInfo(use = Id.NAME, defaultImpl = PullRequestEvent.class)
public final class PullRequestEvent extends GithubEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private long number;
	
	@JsonProperty("pull_request")
	private PullRequest pullRequest;
	
	@JsonProperty(required = false)
	private User assignee;
	
	@JsonProperty(required = false)
	private Label label;
	
	public String getAction() {
		return action;
	}

	public long getNumber() {
		return number;
	}

	public PullRequest getPullRequest() {
		return pullRequest;
	}
	
	public User getAssignee() {
		return assignee;
	}
	
	public Label getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + Objects.hashCode(this.action);
		hash = 37 * hash + (int)(this.number ^ (this.number >>> 32));
		hash = 37 * hash + Objects.hashCode(this.pullRequest);
		hash = 37 * hash + Objects.hashCode(this.assignee);
		hash = 37 * hash + Objects.hashCode(this.label);
		hash = 37 * hash + Objects.hashCode(this.repository);
		hash = 37 * hash + Objects.hashCode(this.organization);
		hash = 37 * hash + Objects.hashCode(this.sender);
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
		final PullRequestEvent other = (PullRequestEvent)obj;
		if (!Objects.equals(this.action, other.action)) {
			return false;
		}
		if (this.number != other.number) {
			return false;
		}
		if (!Objects.equals(this.pullRequest, other.pullRequest)) {
			return false;
		}
		if (!Objects.equals(this.assignee, other.assignee)) {
			return false;
		}
		if (!Objects.equals(this.label, other.label)) {
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
	
	public void setPayload(PullRequestEvent event) {
		this.action = event.action;
		this.number = event.number;
		this.pullRequest = event.pullRequest;
		this.assignee = event.assignee;
		this.label = event.label;
	}
	
}
