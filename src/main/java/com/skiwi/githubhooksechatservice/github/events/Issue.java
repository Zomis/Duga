
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Issue {
	@JsonProperty
	private String url;
	
	@JsonProperty("labels_url")
	private String labelsUrl;
	
	@JsonProperty("comments_url")
	private String commentsUrl;
	
	@JsonProperty("events_url")
	private String eventsUrl;
	
	@JsonProperty("html_url")
	private String htmlUrl;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private long number;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private User user;
	
	@JsonProperty
	private Label[] labels;
	
	@JsonProperty
	private String state;
	
	@JsonProperty
	private boolean locked;
	
	@JsonProperty
	private User assignee;
	
	@JsonProperty
	private Milestone milestone;
	
	@JsonProperty
	private long comments;
	
	@JsonProperty("created_at")
	private String createdAt;
	
	@JsonProperty("updated_at")
	private String updatedAt;
	
	@JsonProperty("closed_at")
	private String closedAt;
	
	@JsonProperty("pull_request")
	private SimplePullRequest pullRequest;
	
	@JsonProperty
	private String body;

	public String getUrl() {
		return url;
	}

	public String getLabelsUrl() {
		return labelsUrl;
	}

	public String getCommentsUrl() {
		return commentsUrl;
	}

	public String getEventsUrl() {
		return eventsUrl;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public long getId() {
		return id;
	}

	public long getNumber() {
		return number;
	}

	public String getTitle() {
		return title;
	}

	public User getUser() {
		return user;
	}

	public List<Label> getLabels() {
		return Arrays.asList(labels);
	}

	public String getState() {
		return state;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public User getAssignee() {
		return assignee;
	}

	public Milestone getMilestone() {
		return milestone;
	}

	public long getComments() {
		return comments;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getClosedAt() {
		return closedAt;
	}
	
	public SimplePullRequest getPullRequest() {
		return pullRequest;
	}

	public String getBody() {
		return body;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + Objects.hashCode(this.url);
		hash = 29 * hash + Objects.hashCode(this.labelsUrl);
		hash = 29 * hash + Objects.hashCode(this.commentsUrl);
		hash = 29 * hash + Objects.hashCode(this.eventsUrl);
		hash = 29 * hash + Objects.hashCode(this.htmlUrl);
		hash = 29 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 29 * hash + (int)(this.number ^ (this.number >>> 32));
		hash = 29 * hash + Objects.hashCode(this.title);
		hash = 29 * hash + Objects.hashCode(this.user);
		hash = 29 * hash + Arrays.deepHashCode(this.labels);
		hash = 29 * hash + Objects.hashCode(this.state);
		hash = 29 * hash + (this.locked ? 1 : 0);
		hash = 29 * hash + Objects.hashCode(this.assignee);
		hash = 29 * hash + Objects.hashCode(this.milestone);
		hash = 29 * hash + (int)(this.comments ^ (this.comments >>> 32));
		hash = 29 * hash + Objects.hashCode(this.createdAt);
		hash = 29 * hash + Objects.hashCode(this.updatedAt);
		hash = 29 * hash + Objects.hashCode(this.closedAt);
		hash = 29 * hash + Objects.hashCode(this.pullRequest);
		hash = 29 * hash + Objects.hashCode(this.body);
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
		final Issue other = (Issue)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.labelsUrl, other.labelsUrl)) {
			return false;
		}
		if (!Objects.equals(this.commentsUrl, other.commentsUrl)) {
			return false;
		}
		if (!Objects.equals(this.eventsUrl, other.eventsUrl)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (this.number != other.number) {
			return false;
		}
		if (!Objects.equals(this.title, other.title)) {
			return false;
		}
		if (!Objects.equals(this.user, other.user)) {
			return false;
		}
		if (!Arrays.deepEquals(this.labels, other.labels)) {
			return false;
		}
		if (!Objects.equals(this.state, other.state)) {
			return false;
		}
		if (this.locked != other.locked) {
			return false;
		}
		if (!Objects.equals(this.assignee, other.assignee)) {
			return false;
		}
		if (!Objects.equals(this.milestone, other.milestone)) {
			return false;
		}
		if (this.comments != other.comments) {
			return false;
		}
		if (!Objects.equals(this.createdAt, other.createdAt)) {
			return false;
		}
		if (!Objects.equals(this.updatedAt, other.updatedAt)) {
			return false;
		}
		if (!Objects.equals(this.closedAt, other.closedAt)) {
			return false;
		}
		if (!Objects.equals(this.pullRequest, other.pullRequest)) {
			return false;
		}
		if (!Objects.equals(this.body, other.body)) {
			return false;
		}
		return true;
	}
}
