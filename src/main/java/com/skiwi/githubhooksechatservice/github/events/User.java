
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class User {
	@JsonProperty
	private String login;
	
    @JsonProperty
    private long id;
    
    @JsonProperty(value = "avatar_url")
    private String avatarUrl;
    
    @JsonProperty(value = "gravatar_id")
    private String gravatarId;
    
    @JsonProperty
    private String url;
    
    @JsonProperty(value = "html_url")
    private String htmlUrl;
    
    @JsonProperty(value = "followers_url")
    private String followersUrl;
    
    @JsonProperty(value = "following_url")
    private String followingUrl;
    
    @JsonProperty(value = "gists_url")
    private String gistsUrl;
    
    @JsonProperty(value = "starred_url")
    private String starredUrl;
    
    @JsonProperty(value = "subscriptions_url")
    private String subscriptionsUrl;
    
    @JsonProperty(value = "organizations_url")
    private String organizationsUrl;
    
    @JsonProperty(value = "repos_url")
    private String reposUrl;
    
    @JsonProperty(value = "events_url")
    private String eventsUrl;
    
    @JsonProperty(value = "received_events_url")
    private String receivedEventsUrl;
    
    @JsonProperty
    private String type;
    
    @JsonProperty(value = "site_admin")
    private boolean siteAdmin;

	public String getLogin() {
		return login;
	}

	public long getId() {
		return id;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public String getGravatarId() {
		return gravatarId;
	}

	public String getUrl() {
		return url;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public String getFollowersUrl() {
		return followersUrl;
	}

	public String getFollowingUrl() {
		return followingUrl;
	}

	public String getGistsUrl() {
		return gistsUrl;
	}

	public String getStarredUrl() {
		return starredUrl;
	}

	public String getSubscriptionsUrl() {
		return subscriptionsUrl;
	}

	public String getOrganizationsUrl() {
		return organizationsUrl;
	}

	public String getReposUrl() {
		return reposUrl;
	}

	public String getEventsUrl() {
		return eventsUrl;
	}

	public String getReceivedEventsUrl() {
		return receivedEventsUrl;
	}

	public String getType() {
		return type;
	}

	public boolean isSiteAdmin() {
		return siteAdmin;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + Objects.hashCode(this.login);
		hash = 29 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 29 * hash + Objects.hashCode(this.avatarUrl);
		hash = 29 * hash + Objects.hashCode(this.gravatarId);
		hash = 29 * hash + Objects.hashCode(this.url);
		hash = 29 * hash + Objects.hashCode(this.htmlUrl);
		hash = 29 * hash + Objects.hashCode(this.followersUrl);
		hash = 29 * hash + Objects.hashCode(this.followingUrl);
		hash = 29 * hash + Objects.hashCode(this.gistsUrl);
		hash = 29 * hash + Objects.hashCode(this.starredUrl);
		hash = 29 * hash + Objects.hashCode(this.subscriptionsUrl);
		hash = 29 * hash + Objects.hashCode(this.organizationsUrl);
		hash = 29 * hash + Objects.hashCode(this.reposUrl);
		hash = 29 * hash + Objects.hashCode(this.eventsUrl);
		hash = 29 * hash + Objects.hashCode(this.receivedEventsUrl);
		hash = 29 * hash + Objects.hashCode(this.type);
		hash = 29 * hash + (this.siteAdmin ? 1 : 0);
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
		final User other = (User)obj;
		if (!Objects.equals(this.login, other.login)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.avatarUrl, other.avatarUrl)) {
			return false;
		}
		if (!Objects.equals(this.gravatarId, other.gravatarId)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (!Objects.equals(this.followersUrl, other.followersUrl)) {
			return false;
		}
		if (!Objects.equals(this.followingUrl, other.followingUrl)) {
			return false;
		}
		if (!Objects.equals(this.gistsUrl, other.gistsUrl)) {
			return false;
		}
		if (!Objects.equals(this.starredUrl, other.starredUrl)) {
			return false;
		}
		if (!Objects.equals(this.subscriptionsUrl, other.subscriptionsUrl)) {
			return false;
		}
		if (!Objects.equals(this.organizationsUrl, other.organizationsUrl)) {
			return false;
		}
		if (!Objects.equals(this.reposUrl, other.reposUrl)) {
			return false;
		}
		if (!Objects.equals(this.eventsUrl, other.eventsUrl)) {
			return false;
		}
		if (!Objects.equals(this.receivedEventsUrl, other.receivedEventsUrl)) {
			return false;
		}
		if (!Objects.equals(this.type, other.type)) {
			return false;
		}
		if (this.siteAdmin != other.siteAdmin) {
			return false;
		}
		return true;
	}
}
