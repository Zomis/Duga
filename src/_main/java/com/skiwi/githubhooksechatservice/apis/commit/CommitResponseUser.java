
package com.skiwi.githubhooksechatservice.apis.commit;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitResponseUser extends AnySetterJSONObject {
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String email;
	
	@JsonProperty
	private String date;

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getDate() {
		return date;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + Objects.hashCode(this.name);
		hash = 53 * hash + Objects.hashCode(this.email);
		hash = 53 * hash + Objects.hashCode(this.date);
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
		final CommitResponseUser other = (CommitResponseUser)obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.email, other.email)) {
			return false;
		}
		if (!Objects.equals(this.date, other.date)) {
			return false;
		}
		return true;
	}
}
