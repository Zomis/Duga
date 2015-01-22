package com.skiwi.githubhooksechatservice.events.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.github.classes.Organization;
import com.skiwi.githubhooksechatservice.events.github.classes.Repository;
import com.skiwi.githubhooksechatservice.events.github.classes.User;

public abstract class GithubEvent extends AbstractEvent {
	
	@JsonProperty
	protected Repository repository;
	
	@JsonProperty(required = false)
	protected Organization organization;
	
	@JsonProperty
	protected User sender;

	public final Repository getRepository() {
		return repository;
	}

	public final Organization getOrganization() {
		return organization;
	}
	
	public final User getSender() {
		return sender;
	}

	public void setRepo(Repository repository) {
		this.repository = repository;
		this.repository.fixUrl();
	}
	
	public void setActor(User user) {
		/* id, login, url */
		this.sender = user;
		this.sender.fixUrl();
	}
	
}
