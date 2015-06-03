
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.skiwi.githubhooksechatservice.events.github.classes.Repository;

/**
 *
 * @author Frank van Heeswijk
 */
@JsonTypeInfo(use = Id.NAME, defaultImpl = ForkEvent.class)
public final class ForkEvent extends GithubEvent {
	@JsonProperty
	private Repository forkee;
	
	public Repository getForkee() {
		return forkee;
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
	
	public void setPayload(ForkEvent event) {
		this.forkee = event.forkee;
	}
	
}
