
package com.skiwi.githubhooksechatservice.github.events;

import com.skiwi.githubhooksechatservice.events.BaseEvent;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class WatchEvent extends BaseEvent {
	@JsonProperty
	private String action;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty(required = false)
	private Organization organization;
	
	@JsonProperty
	private User sender;

	public String getAction() {
		return action;
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
		hash = 37 * hash + Objects.hashCode(this.action);
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
		final WatchEvent other = (WatchEvent)obj;
		if (!Objects.equals(this.action, other.action)) {
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
