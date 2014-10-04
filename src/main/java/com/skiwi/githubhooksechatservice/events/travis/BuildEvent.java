
package com.skiwi.githubhooksechatservice.events.travis;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.skiwi.githubhooksechatservice.events.BaseEvent;

/**
 *
 * @author Frank van Heeswijk
 */
public final class BuildEvent extends BaseEvent {
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String number;
	
	@JsonProperty
	private String status;
	
	@JsonProperty("started_at")
	private String startedAt;
	
	@JsonProperty("finished_at")
	private String finishedAt;
	
	@JsonProperty("status_message")
	private String statusMessage;
	
	@JsonProperty
	private String result;
	
	@JsonProperty("result_message")
	private String resultMessage;
	
	@JsonProperty
	private String duration;
	
	@JsonProperty
	private String commit;
	
	@JsonProperty
	private String branch;
	
	@JsonProperty
	private String message;
	
	@JsonProperty("compare_url")
	private String compareUrl;
	
	@JsonProperty("committed_at")
	private String committedAt;
	
	@JsonProperty("committer_name")
	private String committerName;
	
	@JsonProperty("committer_email")
	private String committerEmail;
	
	@JsonProperty("author_name")
	private String authorName;
	
	@JsonProperty("author_email")
	private String authorEmail;
	
	@JsonProperty
	private String type;
	
	@JsonProperty(value = "pull_request_number", required = false)
	private long pullRequestNumber;
	
	@JsonProperty("build_url")
	private String buildUrl;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty
	private JsonNode config;
	
	@JsonProperty
	private JsonNode matrix;

	public long getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public String getStatus() {
		return status;
	}

	public String getStartedAt() {
		return startedAt;
	}

	public String getFinishedAt() {
		return finishedAt;
	}

	public String getStatusMessage() {
		return statusMessage;
	}
	
	public String getResult() {
		return result;
	}
	
	public String getResultMessage() {
		return resultMessage;
	}
	
	public String getDuration() {
		return duration;
	}

	public String getCommit() {
		return commit;
	}

	public String getBranch() {
		return branch;
	}

	public String getMessage() {
		return message;
	}

	public String getCompareUrl() {
		return compareUrl;
	}

	public String getCommittedAt() {
		return committedAt;
	}

	public String getCommitterName() {
		return committerName;
	}

	public String getCommitterEmail() {
		return committerEmail;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public String getType() {
		return type;
	}

	public long getPullRequestNumber() {
		return pullRequestNumber;
	}

	public String getBuildUrl() {
		return buildUrl;
	}

	public Repository getRepository() {
		return repository;
	}

	public JsonNode getConfig() {
		return config;
	}

	public JsonNode getMatrix() {
		return matrix;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 17 * hash + Objects.hashCode(this.number);
		hash = 17 * hash + Objects.hashCode(this.status);
		hash = 17 * hash + Objects.hashCode(this.startedAt);
		hash = 17 * hash + Objects.hashCode(this.finishedAt);
		hash = 17 * hash + Objects.hashCode(this.statusMessage);
		hash = 17 * hash + Objects.hashCode(this.result);
		hash = 17 * hash + Objects.hashCode(this.resultMessage);
		hash = 17 * hash + Objects.hashCode(this.duration);
		hash = 17 * hash + Objects.hashCode(this.commit);
		hash = 17 * hash + Objects.hashCode(this.branch);
		hash = 17 * hash + Objects.hashCode(this.message);
		hash = 17 * hash + Objects.hashCode(this.compareUrl);
		hash = 17 * hash + Objects.hashCode(this.committedAt);
		hash = 17 * hash + Objects.hashCode(this.committerName);
		hash = 17 * hash + Objects.hashCode(this.committerEmail);
		hash = 17 * hash + Objects.hashCode(this.authorName);
		hash = 17 * hash + Objects.hashCode(this.authorEmail);
		hash = 17 * hash + Objects.hashCode(this.type);
		hash = 17 * hash + (int)(this.pullRequestNumber ^ (this.pullRequestNumber >>> 32));
		hash = 17 * hash + Objects.hashCode(this.buildUrl);
		hash = 17 * hash + Objects.hashCode(this.repository);
		hash = 17 * hash + Objects.hashCode(this.config);
		hash = 17 * hash + Objects.hashCode(this.matrix);
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
		final BuildEvent other = (BuildEvent)obj;
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.number, other.number)) {
			return false;
		}
		if (!Objects.equals(this.status, other.status)) {
			return false;
		}
		if (!Objects.equals(this.startedAt, other.startedAt)) {
			return false;
		}
		if (!Objects.equals(this.finishedAt, other.finishedAt)) {
			return false;
		}
		if (!Objects.equals(this.statusMessage, other.statusMessage)) {
			return false;
		}
		if (!Objects.equals(this.result, other.result)) {
			return false;
		}
		if (!Objects.equals(this.resultMessage, other.resultMessage)) {
			return false;
		}
		if (!Objects.equals(this.duration, other.duration)) {
			return false;
		}
		if (!Objects.equals(this.commit, other.commit)) {
			return false;
		}
		if (!Objects.equals(this.branch, other.branch)) {
			return false;
		}
		if (!Objects.equals(this.message, other.message)) {
			return false;
		}
		if (!Objects.equals(this.compareUrl, other.compareUrl)) {
			return false;
		}
		if (!Objects.equals(this.committedAt, other.committedAt)) {
			return false;
		}
		if (!Objects.equals(this.committerName, other.committerName)) {
			return false;
		}
		if (!Objects.equals(this.committerEmail, other.committerEmail)) {
			return false;
		}
		if (!Objects.equals(this.authorName, other.authorName)) {
			return false;
		}
		if (!Objects.equals(this.authorEmail, other.authorEmail)) {
			return false;
		}
		if (!Objects.equals(this.type, other.type)) {
			return false;
		}
		if (this.pullRequestNumber != other.pullRequestNumber) {
			return false;
		}
		if (!Objects.equals(this.buildUrl, other.buildUrl)) {
			return false;
		}
		if (!Objects.equals(this.repository, other.repository)) {
			return false;
		}
		if (!Objects.equals(this.config, other.config)) {
			return false;
		}
		if (!Objects.equals(this.matrix, other.matrix)) {
			return false;
		}
		return true;
	}
}
