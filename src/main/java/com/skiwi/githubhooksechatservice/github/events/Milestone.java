
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Milestone {
	@JsonProperty
	private String url;
	
	@JsonProperty("labels_url")
	private String labelsUrl;
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private long number;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	private User creator;
	
	@JsonProperty("open_issues")
	private long openIssues;
	
	@JsonProperty("closed_issues")
	private long closedIssues;
	
	@JsonProperty
	private String state;
	
	@JsonProperty("created_at")
	private String createdAt;
	
	@JsonProperty("updated_at")
	private String updatedAt;
	
	@JsonProperty("due_on")
	private String dueOn;
	

	public String getUrl() {
		return url;
	}

	public String getLabelsUrl() {
		return labelsUrl;
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

	public String getDescription() {
		return description;
	}

	public User getCreator() {
		return creator;
	}

	public long getOpenIssues() {
		return openIssues;
	}

	public long getClosedIssues() {
		return closedIssues;
	}

	public String getState() {
		return state;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getDueOn() {
		return dueOn;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 61 * hash + Objects.hashCode(this.url);
		hash = 61 * hash + Objects.hashCode(this.labelsUrl);
		hash = 61 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 61 * hash + (int)(this.number ^ (this.number >>> 32));
		hash = 61 * hash + Objects.hashCode(this.title);
		hash = 61 * hash + Objects.hashCode(this.description);
		hash = 61 * hash + Objects.hashCode(this.creator);
		hash = 61 * hash + (int)(this.openIssues ^ (this.openIssues >>> 32));
		hash = 61 * hash + (int)(this.closedIssues ^ (this.closedIssues >>> 32));
		hash = 61 * hash + Objects.hashCode(this.state);
		hash = 61 * hash + Objects.hashCode(this.createdAt);
		hash = 61 * hash + Objects.hashCode(this.updatedAt);
		hash = 61 * hash + Objects.hashCode(this.dueOn);
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
		final Milestone other = (Milestone)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.labelsUrl, other.labelsUrl)) {
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
		if (!Objects.equals(this.description, other.description)) {
			return false;
		}
		if (!Objects.equals(this.creator, other.creator)) {
			return false;
		}
		if (this.openIssues != other.openIssues) {
			return false;
		}
		if (this.closedIssues != other.closedIssues) {
			return false;
		}
		if (!Objects.equals(this.state, other.state)) {
			return false;
		}
		if (!Objects.equals(this.createdAt, other.createdAt)) {
			return false;
		}
		if (!Objects.equals(this.updatedAt, other.updatedAt)) {
			return false;
		}
		if (!Objects.equals(this.dueOn, other.dueOn)) {
			return false;
		}
		return true;
	}
}
