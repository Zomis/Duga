
package com.skiwi.githubhooksechatservice.events.github.classes;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class LegacyUser extends AnySetterJSONObject {
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String email;
	
	@JsonProperty
	private String username;

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + Objects.hashCode(this.name);
		hash = 17 * hash + Objects.hashCode(this.email);
		hash = 17 * hash + Objects.hashCode(this.username);
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
		final LegacyUser other = (LegacyUser)obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.email, other.email)) {
			return false;
		}
		if (!Objects.equals(this.username, other.username)) {
			return false;
		}
		return true;
	}
}
