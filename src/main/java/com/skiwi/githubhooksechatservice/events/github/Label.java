
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Label extends AnySetterJSONObject {
	@JsonProperty
	private String url;
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String color;

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + Objects.hashCode(this.url);
		hash = 37 * hash + Objects.hashCode(this.name);
		hash = 37 * hash + Objects.hashCode(this.color);
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
		final Label other = (Label)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.color, other.color)) {
			return false;
		}
		return true;
	}
}
