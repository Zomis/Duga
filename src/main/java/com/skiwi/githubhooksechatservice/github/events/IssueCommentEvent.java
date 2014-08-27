
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class IssueCommentEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private Issue issue;
	
	@JsonProperty
	private Comment comment;
	
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

	public Comment getComment() {
		return comment;
	}

	public Repository getRepository() {
		return repository;
	}

	public User getSender() {
		return sender;
	}
}
