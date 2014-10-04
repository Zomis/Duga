
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Team extends AnySetterJSONObject {
	@JsonProperty
	private String name;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String slug;
	
	@JsonProperty
	private String permission;
	
	@JsonProperty
	private String url;
	
	@JsonProperty("members_url")
	private String membersUrl;
	
	@JsonProperty("repositories_url")
	private String repositoriesUrl;

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}

	public String getSlug() {
		return slug;
	}

	public String getPermission() {
		return permission;
	}

	public String getUrl() {
		return url;
	}

	public String getMembersUrl() {
		return membersUrl;
	}

	public String getRepositoriesUrl() {
		return repositoriesUrl;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(this.name);
		hash = 79 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 79 * hash + Objects.hashCode(this.slug);
		hash = 79 * hash + Objects.hashCode(this.permission);
		hash = 79 * hash + Objects.hashCode(this.url);
		hash = 79 * hash + Objects.hashCode(this.membersUrl);
		hash = 79 * hash + Objects.hashCode(this.repositoriesUrl);
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
		final Team other = (Team)obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.slug, other.slug)) {
			return false;
		}
		if (!Objects.equals(this.permission, other.permission)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.membersUrl, other.membersUrl)) {
			return false;
		}
		if (!Objects.equals(this.repositoriesUrl, other.repositoriesUrl)) {
			return false;
		}
		return true;
	}
}
