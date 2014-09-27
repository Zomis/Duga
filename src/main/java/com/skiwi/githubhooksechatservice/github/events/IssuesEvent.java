
package com.skiwi.githubhooksechatservice.github.events;

import com.skiwi.githubhooksechatservice.events.BaseEvent;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class IssuesEvent extends BaseEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private Issue issue;
	
	@JsonProperty(required = false)
	private User assignee;
	
	@JsonProperty(required = false)
	private Label label;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty(required = false)
	private Organization organization;
	
	@JsonProperty
	private User sender;

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

	public Repository getRepository() {
		return repository;
	}

	public Organization getOrganization() {
		return organization;
	}

	public User getSender() {
		return sender;
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
}
