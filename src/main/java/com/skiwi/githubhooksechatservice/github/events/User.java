
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class User {
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
}
