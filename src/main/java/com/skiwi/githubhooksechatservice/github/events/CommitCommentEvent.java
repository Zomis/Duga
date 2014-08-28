
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitCommentEvent {
	@JsonProperty
	private CommitComment comment;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty
	private User sender;

	public CommitComment getComment() {
		return comment;
	}

	public Repository getRepository() {
		return repository;
	}

	public User getSender() {
		return sender;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(this.comment);
		hash = 79 * hash + Objects.hashCode(this.repository);
		hash = 79 * hash + Objects.hashCode(this.sender);
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
		final CommitCommentEvent other = (CommitCommentEvent)obj;
		if (!Objects.equals(this.comment, other.comment)) {
			return false;
		}
		if (!Objects.equals(this.repository, other.repository)) {
			return false;
		}
		if (!Objects.equals(this.sender, other.sender)) {
			return false;
		}
		return true;
	}
}
