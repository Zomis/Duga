
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class PullRequest {
	@JsonProperty
	private String url;
	
	@JsonProperty
	private long id;
	
	@JsonProperty("html_url")
	private String htmlUrl;
	
	@JsonProperty("diff_url")
	private String diffUrl;
	
	@JsonProperty("patch_url")
	private String patchUrl;
	
	@JsonProperty("issue_url")
	private String issueUrl;
	
	@JsonProperty
	private long number;
	
	@JsonProperty
	private String state;
	
	@JsonProperty
	private boolean locked;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private User user;
	
	@JsonProperty
	private String body;
	
	@JsonProperty("created_at")
	private String createdAt;
	
	@JsonProperty("updated_at")
	private String updatedAt;
	
	@JsonProperty("closed_at")
	private String closedAt;
	
	@JsonProperty("merged_at")
	private String mergedAt;
	
	@JsonProperty("merge_commit_sha")
	private String mergeCommitSha;
	
	@JsonProperty
	private User assignee;
	
	@JsonProperty
	private Milestone milestone;
	
	@JsonProperty("commits_url")
	private String commitsUrl;
	
	@JsonProperty("review_comments_url")
	private String reviewCommentsUrl;
	
	@JsonProperty("review_comment_url")
	private String reviewCommentUrl;
	
	@JsonProperty("comments_url")
	private String commentsUrl;
	
	@JsonProperty("statuses_url")
	private String statusesUrl;
	
	@JsonProperty
	private Commit head;
	
	@JsonProperty
	private Commit base;
	
	@JsonProperty("_links")
	private PullRequestLinks links;
	
	@JsonProperty
	private boolean merged;
	
	@JsonProperty
	private String mergeable;
	
	@JsonProperty("mergeable_state")
	private String mergeableState;
	
	@JsonProperty("merged_by")
	private User mergedBy;
	
	@JsonProperty
	private long comments;
	
	@JsonProperty("review_comments")
	private long reviewComments;
	
	@JsonProperty
	private long commits;
	
	@JsonProperty
	private long additions;
	
	@JsonProperty
	private long deletions;
	
	@JsonProperty("changed_files")
	private long changedFiles;

	public String getUrl() {
		return url;
	}

	public long getId() {
		return id;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public String getDiffUrl() {
		return diffUrl;
	}

	public String getPatchUrl() {
		return patchUrl;
	}

	public String getIssueUrl() {
		return issueUrl;
	}

	public long getNumber() {
		return number;
	}

	public String getState() {
		return state;
	}
	
	public boolean isLocked() {
		return locked;
	}

	public String getTitle() {
		return title;
	}

	public User getUser() {
		return user;
	}

	public String getBody() {
		return body;
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

	public String getMergedAt() {
		return mergedAt;
	}

	public String getMergeCommitSha() {
		return mergeCommitSha;
	}

	public User getAssignee() {
		return assignee;
	}

	public Milestone getMilestone() {
		return milestone;
	}

	public String getCommitsUrl() {
		return commitsUrl;
	}

	public String getReviewCommentsUrl() {
		return reviewCommentsUrl;
	}

	public String getReviewCommentUrl() {
		return reviewCommentUrl;
	}

	public String getCommentsUrl() {
		return commentsUrl;
	}

	public String getStatusesUrl() {
		return statusesUrl;
	}

	public Commit getHead() {
		return head;
	}

	public Commit getBase() {
		return base;
	}

	public PullRequestLinks getLinks() {
		return links;
	}

	public boolean isMerged() {
		return merged;
	}

	public String getMergeable() {
		return mergeable;
	}

	public String getMergeableState() {
		return mergeableState;
	}

	public User getMergedBy() {
		return mergedBy;
	}

	public long getComments() {
		return comments;
	}

	public long getReviewComments() {
		return reviewComments;
	}

	public long getCommits() {
		return commits;
	}

	public long getAdditions() {
		return additions;
	}

	public long getDeletions() {
		return deletions;
	}

	public long getChangedFiles() {
		return changedFiles;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(this.url);
		hash = 79 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 79 * hash + Objects.hashCode(this.htmlUrl);
		hash = 79 * hash + Objects.hashCode(this.diffUrl);
		hash = 79 * hash + Objects.hashCode(this.patchUrl);
		hash = 79 * hash + Objects.hashCode(this.issueUrl);
		hash = 79 * hash + (int)(this.number ^ (this.number >>> 32));
		hash = 79 * hash + Objects.hashCode(this.state);
		hash = 79 * hash + (this.locked ? 1 : 0);
		hash = 79 * hash + Objects.hashCode(this.title);
		hash = 79 * hash + Objects.hashCode(this.user);
		hash = 79 * hash + Objects.hashCode(this.body);
		hash = 79 * hash + Objects.hashCode(this.createdAt);
		hash = 79 * hash + Objects.hashCode(this.updatedAt);
		hash = 79 * hash + Objects.hashCode(this.closedAt);
		hash = 79 * hash + Objects.hashCode(this.mergedAt);
		hash = 79 * hash + Objects.hashCode(this.mergeCommitSha);
		hash = 79 * hash + Objects.hashCode(this.assignee);
		hash = 79 * hash + Objects.hashCode(this.milestone);
		hash = 79 * hash + Objects.hashCode(this.commitsUrl);
		hash = 79 * hash + Objects.hashCode(this.reviewCommentsUrl);
		hash = 79 * hash + Objects.hashCode(this.reviewCommentUrl);
		hash = 79 * hash + Objects.hashCode(this.commentsUrl);
		hash = 79 * hash + Objects.hashCode(this.statusesUrl);
		hash = 79 * hash + Objects.hashCode(this.head);
		hash = 79 * hash + Objects.hashCode(this.base);
		hash = 79 * hash + Objects.hashCode(this.links);
		hash = 79 * hash + (this.merged ? 1 : 0);
		hash = 79 * hash + Objects.hashCode(this.mergeable);
		hash = 79 * hash + Objects.hashCode(this.mergeableState);
		hash = 79 * hash + Objects.hashCode(this.mergedBy);
		hash = 79 * hash + (int)(this.comments ^ (this.comments >>> 32));
		hash = 79 * hash + (int)(this.reviewComments ^ (this.reviewComments >>> 32));
		hash = 79 * hash + (int)(this.commits ^ (this.commits >>> 32));
		hash = 79 * hash + (int)(this.additions ^ (this.additions >>> 32));
		hash = 79 * hash + (int)(this.deletions ^ (this.deletions >>> 32));
		hash = 79 * hash + (int)(this.changedFiles ^ (this.changedFiles >>> 32));
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
		final PullRequest other = (PullRequest)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (!Objects.equals(this.diffUrl, other.diffUrl)) {
			return false;
		}
		if (!Objects.equals(this.patchUrl, other.patchUrl)) {
			return false;
		}
		if (!Objects.equals(this.issueUrl, other.issueUrl)) {
			return false;
		}
		if (this.number != other.number) {
			return false;
		}
		if (!Objects.equals(this.state, other.state)) {
			return false;
		}
		if (this.locked != other.locked) {
			return false;
		}
		if (!Objects.equals(this.title, other.title)) {
			return false;
		}
		if (!Objects.equals(this.user, other.user)) {
			return false;
		}
		if (!Objects.equals(this.body, other.body)) {
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
		if (!Objects.equals(this.mergedAt, other.mergedAt)) {
			return false;
		}
		if (!Objects.equals(this.mergeCommitSha, other.mergeCommitSha)) {
			return false;
		}
		if (!Objects.equals(this.assignee, other.assignee)) {
			return false;
		}
		if (!Objects.equals(this.milestone, other.milestone)) {
			return false;
		}
		if (!Objects.equals(this.commitsUrl, other.commitsUrl)) {
			return false;
		}
		if (!Objects.equals(this.reviewCommentsUrl, other.reviewCommentsUrl)) {
			return false;
		}
		if (!Objects.equals(this.reviewCommentUrl, other.reviewCommentUrl)) {
			return false;
		}
		if (!Objects.equals(this.commentsUrl, other.commentsUrl)) {
			return false;
		}
		if (!Objects.equals(this.statusesUrl, other.statusesUrl)) {
			return false;
		}
		if (!Objects.equals(this.head, other.head)) {
			return false;
		}
		if (!Objects.equals(this.base, other.base)) {
			return false;
		}
		if (!Objects.equals(this.links, other.links)) {
			return false;
		}
		if (this.merged != other.merged) {
			return false;
		}
		if (!Objects.equals(this.mergeable, other.mergeable)) {
			return false;
		}
		if (!Objects.equals(this.mergeableState, other.mergeableState)) {
			return false;
		}
		if (!Objects.equals(this.mergedBy, other.mergedBy)) {
			return false;
		}
		if (this.comments != other.comments) {
			return false;
		}
		if (this.reviewComments != other.reviewComments) {
			return false;
		}
		if (this.commits != other.commits) {
			return false;
		}
		if (this.additions != other.additions) {
			return false;
		}
		if (this.deletions != other.deletions) {
			return false;
		}
		if (this.changedFiles != other.changedFiles) {
			return false;
		}
		return true;
	}
}
