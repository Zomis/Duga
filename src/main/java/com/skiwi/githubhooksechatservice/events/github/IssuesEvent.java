
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.skiwi.githubhooksechatservice.events.github.classes.Issue;
import com.skiwi.githubhooksechatservice.events.github.classes.Label;
import com.skiwi.githubhooksechatservice.events.github.classes.User;

/**
 *
 * @author Frank van Heeswijk
 */
@JsonTypeInfo(use = Id.NAME, defaultImpl = IssuesEvent.class)
public final class IssuesEvent extends GithubEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private Issue issue;
	
	@JsonProperty(required = false)
	private User assignee;
	
	@JsonProperty(required = false)
	private Label label;
	
	public String getAction() {
		return action;
	}

	public Issue getIssue() {
		return issue;
	}
	
	public User getAssignee() {
		return assignee;
	}
	
	public Label getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + Objects.hashCode(this.action);
		hash = 71 * hash + Objects.hashCode(this.issue);
		hash = 71 * hash + Objects.hashCode(this.assignee);
		hash = 71 * hash + Objects.hashCode(this.label);
		hash = 71 * hash + Objects.hashCode(this.repository);
		hash = 71 * hash + Objects.hashCode(this.organization);
		hash = 71 * hash + Objects.hashCode(this.sender);
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
		final IssuesEvent other = (IssuesEvent)obj;
		if (!Objects.equals(this.action, other.action)) {
			return false;
		}
		if (!Objects.equals(this.issue, other.issue)) {
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

	public void setPayload(IssuesEvent event) {
		this.action = event.action;
		this.assignee = event.assignee;
		this.issue = event.issue;
		this.sender = event.sender;
		this.label = event.label;
	}
	
	@Override
	public String toString() {
		return "IssuesEvent [action=" + action + ", issue=" + issue
				+ ", assignee=" + assignee + ", label=" + label + ", sender="
				+ sender + ", repository=" + repository + ", organization="
				+ organization + "]";
	}
	
}
