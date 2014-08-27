
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class CommitCommentEvent {
	@JsonProperty
	private CommitComment comment;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty
	private User sender;

	public CommitComment getComment() {
		return comment;
	}

	public Repository getRepository() {
		return repository;
	}

	public User getSender() {
		return sender;
	}
}
