
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class WatchEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty
	private User sender;

	public String getAction() {
		return action;
	}

	public Repository getRepository() {
		return repository;
	}

	public User getSender() {
		return sender;
	}
}
