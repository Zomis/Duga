package com.skiwi.githubhooksechatservice.events.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.github.classes.Organization;
import com.skiwi.githubhooksechatservice.events.github.classes.Repository;

public abstract class GithubEvent extends AbstractEvent {
	
	@JsonProperty
	protected Repository repository;
	
	@JsonProperty(required = false)
	protected Organization organization;
	
	public final Repository getRepository() {
		return repository;
	}

	public final Organization getOrganization() {
		return organization;
	}
	
	public void setRepo(Repository repository) {
		this.repository = repository;
	}

}
