
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class TeamAddEvent {
	@JsonProperty
	private Team team;
	
	@JsonProperty(required = false)
	private User user;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty
	private User sender;

	public Team getTeam() {
		return team;
	}

	public User getUser() {
		return user;
	}

	public Repository getRepository() {
		return repository;
	}

	public User getSender() {
		return sender;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 17 * hash + Objects.hashCode(this.team);
		hash = 17 * hash + Objects.hashCode(this.user);
		hash = 17 * hash + Objects.hashCode(this.repository);
		hash = 17 * hash + Objects.hashCode(this.sender);
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
		final TeamAddEvent other = (TeamAddEvent)obj;
		if (!Objects.equals(this.team, other.team)) {
			return false;
		}
		if (!Objects.equals(this.user, other.user)) {
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
