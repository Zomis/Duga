
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 *
 * @author Simon Forsberg
 */
@JsonTypeInfo(use = Id.NAME, defaultImpl = PublicEvent.class)
public final class PublicEvent extends GithubEvent {

	@JsonProperty("public")
	private boolean publicSet;
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Boolean.hashCode(publicSet);
		hash = 59 * hash + Objects.hashCode(this.repository);
		hash = 59 * hash + Objects.hashCode(this.organization);
		hash = 59 * hash + Objects.hashCode(this.sender);
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
		final PublicEvent other = (PublicEvent)obj;
		if (this.publicSet != other.publicSet) {
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
	
	public boolean isPublic() {
		return publicSet;
	}
	
}
