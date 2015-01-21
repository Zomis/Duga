
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.github.classes.Repository;
import com.skiwi.githubhooksechatservice.events.github.classes.User;

/**
 *
 * @author Frank van Heeswijk
 */
public final class ForkEvent extends GithubEvent {
	@JsonProperty
	private Repository forkee;
	
	@JsonProperty
	private User sender;

	public Repository getForkee() {
		return forkee;
	}

	public User getSender() {
		return sender;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 11 * hash + Objects.hashCode(this.forkee);
		hash = 11 * hash + Objects.hashCode(this.repository);
		hash = 11 * hash + Objects.hashCode(this.organization);
		hash = 11 * hash + Objects.hashCode(this.sender);
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
		final ForkEvent other = (ForkEvent)obj;
		if (!Objects.equals(this.forkee, other.forkee)) {
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
