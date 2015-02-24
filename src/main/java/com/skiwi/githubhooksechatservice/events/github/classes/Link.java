
package com.skiwi.githubhooksechatservice.events.github.classes;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Link extends AnySetterJSONObject {
	@JsonProperty
	private String href;

	public String getHref() {
		return href;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.href);
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
		final Link other = (Link)obj;
		if (!Objects.equals(this.href, other.href)) {
			return false;
		}
		return true;
	}
}
