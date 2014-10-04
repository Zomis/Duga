
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Commit extends AnySetterJSONObject {
	@JsonProperty
	private String label;
	
	@JsonProperty
	private String ref;
	
	@JsonProperty
	private String sha;
	
	@JsonProperty
	private User user;
	
	@JsonProperty
	private Repository repo;

	public String getLabel() {
		return label;
	}

	public String getRef() {
		return ref;
	}

	public String getSha() {
		return sha;
	}

	public User getUser() {
		return user;
	}

	public Repository getRepo() {
		return repo;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.label);
		hash = 89 * hash + Objects.hashCode(this.ref);
		hash = 89 * hash + Objects.hashCode(this.sha);
		hash = 89 * hash + Objects.hashCode(this.user);
		hash = 89 * hash + Objects.hashCode(this.repo);
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
		final Commit other = (Commit)obj;
		if (!Objects.equals(this.label, other.label)) {
			return false;
		}
		if (!Objects.equals(this.ref, other.ref)) {
			return false;
		}
		if (!Objects.equals(this.sha, other.sha)) {
			return false;
		}
		if (!Objects.equals(this.user, other.user)) {
			return false;
		}
		if (!Objects.equals(this.repo, other.repo)) {
			return false;
		}
		return true;
	}
}
