
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class IssuesEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private Issue issue;
	
	@JsonProperty(required = false)
	private User assignee;
	
	@JsonProperty(required = false)
	private IssueLabel label;
	
	@JsonProperty
	private Repository repository;
	
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
	
	public IssueLabel getLabel() {
		return label;
	}

	public Repository getRepository() {
		return repository;
	}

	public User getSender() {
		return sender;
	}
}
