
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class Account {
    @JsonProperty(required = false)
    private String name;
    
    @JsonProperty(required = false)
    private String email;
    
    @JsonProperty(required = false)
    private String username;
	
	@JsonProperty(required = false)
	private String login;
	
    @JsonProperty(required = false)
    private long id;
    
    @JsonProperty(value = "avatar_url", required = false)
    private String avatarUrl;
    
    @JsonProperty(value = "gravatar_id", required = false)
    private String gravatarId;
    
    @JsonProperty(required = false)
    private String url;
    
    @JsonProperty(value = "html_url", required = false)
    private String htmlUrl;
    
    @JsonProperty(value = "followers_url", required = false)
    private String followersUrl;
    
    @JsonProperty(value = "following_url", required = false)
    private String followingUrl;
    
    @JsonProperty(value = "gists_url", required = false)
    private String gistsUrl;
    
    @JsonProperty(value = "starred_url", required = false)
    private String starredUrl;
    
    @JsonProperty(value = "subscriptions_url", required = false)
    private String subscriptionsUrl;
    
    @JsonProperty(value = "organizations_url", required = false)
    private String organizationsUrl;
    
    @JsonProperty(value = "repos_url", required = false)
    private String reposUrl;
    
    @JsonProperty(value = "events_url", required = false)
    private String eventsUrl;
    
    @JsonProperty(value = "received_events_url", required = false)
    private String receivedEventsUrl;
    
    @JsonProperty(required = false)
    private String type;
    
    @JsonProperty(value = "site_admin", required = false)
    private boolean siteAdmin;

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

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
	
	public String getRealName() {
		if (username != null) {
			return username;
		}
		else if (login != null) {
			return login;
		}
		return name;
	}
}
