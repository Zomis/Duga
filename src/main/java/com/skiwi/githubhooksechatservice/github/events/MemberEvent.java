
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class MemberEvent {
	@JsonProperty
	private User member;
	
	@JsonProperty
	private String action;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty
	private User sender;

	public User getMember() {
		return member;
	}

	public String getAction() {
		return action;
	}

	public Repository getRepository() {
		return repository;
	}

	public User getSender() {
		return sender;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + Objects.hashCode(this.member);
		hash = 89 * hash + Objects.hashCode(this.action);
		hash = 89 * hash + Objects.hashCode(this.repository);
		hash = 89 * hash + Objects.hashCode(this.sender);
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
		final MemberEvent other = (MemberEvent)obj;
		if (!Objects.equals(this.member, other.member)) {
			return false;
		}
		if (!Objects.equals(this.action, other.action)) {
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
