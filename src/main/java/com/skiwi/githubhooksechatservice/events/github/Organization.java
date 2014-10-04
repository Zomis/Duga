
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Organization extends AnySetterJSONObject {
	@JsonProperty
	private String login;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String url;
	
	@JsonProperty("repos_url")
	private String reposUrl;
	
	@JsonProperty("events_url")
	private String eventsUrl;
	
	@JsonProperty("members_url")
	private String membersUrl;
	
	@JsonProperty("public_members_url")
	private String publicMembersUrl;
	
	@JsonProperty("avatar_url")
	private String avatarUrl;

	public String getLogin() {
		return login;
	}

	public long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getReposUrl() {
		return reposUrl;
	}

	public String getEventsUrl() {
		return eventsUrl;
	}

	public String getMembersUrl() {
		return membersUrl;
	}

	public String getPublicMembersUrl() {
		return publicMembersUrl;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + Objects.hashCode(this.login);
		hash = 71 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 71 * hash + Objects.hashCode(this.url);
		hash = 71 * hash + Objects.hashCode(this.reposUrl);
		hash = 71 * hash + Objects.hashCode(this.eventsUrl);
		hash = 71 * hash + Objects.hashCode(this.membersUrl);
		hash = 71 * hash + Objects.hashCode(this.publicMembersUrl);
		hash = 71 * hash + Objects.hashCode(this.avatarUrl);
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
		final Organization other = (Organization)obj;
		if (!Objects.equals(this.login, other.login)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.reposUrl, other.reposUrl)) {
			return false;
		}
		if (!Objects.equals(this.eventsUrl, other.eventsUrl)) {
			return false;
		}
		if (!Objects.equals(this.membersUrl, other.membersUrl)) {
			return false;
		}
		if (!Objects.equals(this.publicMembersUrl, other.publicMembersUrl)) {
			return false;
		}
		if (!Objects.equals(this.avatarUrl, other.avatarUrl)) {
			return false;
		}
		return true;
	}
}
