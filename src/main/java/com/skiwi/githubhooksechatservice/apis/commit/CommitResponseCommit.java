
package com.skiwi.githubhooksechatservice.apis.commit;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitResponseCommit extends AnySetterJSONObject {
	@JsonProperty
	private CommitResponseUser author;
	
	@JsonProperty
	private CommitResponseUser committer;
	
	@JsonProperty
	private String message;
	
	@JsonProperty
	private CommitResponseCommitTree tree;
	
	@JsonProperty
	private String url;
	
	@JsonProperty("comment_count")
	private long commentCount;

	public CommitResponseUser getAuthor() {
		return author;
	}

	public CommitResponseUser getCommitter() {
		return committer;
	}

	public String getMessage() {
		return message;
	}

	public CommitResponseCommitTree getTree() {
		return tree;
	}

	public String getUrl() {
		return url;
	}

	public long getCommentCount() {
		return commentCount;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * hash + Objects.hashCode(this.author);
		hash = 83 * hash + Objects.hashCode(this.committer);
		hash = 83 * hash + Objects.hashCode(this.message);
		hash = 83 * hash + Objects.hashCode(this.tree);
		hash = 83 * hash + Objects.hashCode(this.url);
		hash = 83 * hash + (int)(this.commentCount ^ (this.commentCount >>> 32));
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
		final CommitResponseCommit other = (CommitResponseCommit)obj;
		if (!Objects.equals(this.author, other.author)) {
			return false;
		}
		if (!Objects.equals(this.committer, other.committer)) {
			return false;
		}
		if (!Objects.equals(this.message, other.message)) {
			return false;
		}
		if (!Objects.equals(this.tree, other.tree)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (this.commentCount != other.commentCount) {
			return false;
		}
		return true;
	}
}
