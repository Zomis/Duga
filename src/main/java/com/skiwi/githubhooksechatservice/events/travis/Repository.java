
package com.skiwi.githubhooksechatservice.events.travis;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Repository extends AnySetterJSONObject {
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String name;
	
	@JsonProperty("owner_name")
	private String ownerName;
	
	@JsonProperty
	private String url;

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 17 * hash + Objects.hashCode(this.name);
		hash = 17 * hash + Objects.hashCode(this.ownerName);
		hash = 17 * hash + Objects.hashCode(this.url);
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
		final Repository other = (Repository)obj;
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.ownerName, other.ownerName)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		return true;
	}

	public String getFullNameGithubStyle() {
		return getOwnerName() + "/" + getName();
	}
}
