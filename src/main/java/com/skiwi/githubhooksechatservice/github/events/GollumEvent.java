
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class GollumEvent {
	@JsonProperty
	private WikiPage[] pages;
	
	@JsonProperty
	private Repository repository;
	
	@JsonProperty
	private User sender;

	public List<WikiPage> getPages() {
		return Arrays.asList(pages);
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
		hash = 59 * hash + Arrays.deepHashCode(this.pages);
		hash = 59 * hash + Objects.hashCode(this.repository);
		hash = 59 * hash + Objects.hashCode(this.sender);
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
		final GollumEvent other = (GollumEvent)obj;
		if (!Arrays.deepEquals(this.pages, other.pages)) {
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
