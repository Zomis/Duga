
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Hook {
    @JsonProperty
    private String url;
    
    @JsonProperty("test_url")
    private String testUrl;
    
    @JsonProperty
    private long id;
    
    @JsonProperty
    private String name;
    
    @JsonProperty
    private boolean active;
    
    @JsonProperty
    private String[] events;
    
    @JsonProperty
    private HookConfig config;
    
    @JsonProperty("last_response")
    private Response lastResponse;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("created_at")
    private String createdAt;

    public String getUrl() {
        return url;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<String> getEvents() {
        return Arrays.asList(events);
    }

    public HookConfig getConfig() {
        return config;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.url);
		hash = 97 * hash + Objects.hashCode(this.testUrl);
		hash = 97 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 97 * hash + Objects.hashCode(this.name);
		hash = 97 * hash + (this.active ? 1 : 0);
		hash = 97 * hash + Arrays.deepHashCode(this.events);
		hash = 97 * hash + Objects.hashCode(this.config);
		hash = 97 * hash + Objects.hashCode(this.lastResponse);
		hash = 97 * hash + Objects.hashCode(this.updatedAt);
		hash = 97 * hash + Objects.hashCode(this.createdAt);
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
		final Hook other = (Hook)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.testUrl, other.testUrl)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (this.active != other.active) {
			return false;
		}
		if (!Arrays.deepEquals(this.events, other.events)) {
			return false;
		}
		if (!Objects.equals(this.config, other.config)) {
			return false;
		}
		if (!Objects.equals(this.lastResponse, other.lastResponse)) {
			return false;
		}
		if (!Objects.equals(this.updatedAt, other.updatedAt)) {
			return false;
		}
		if (!Objects.equals(this.createdAt, other.createdAt)) {
			return false;
		}
		return true;
	}
}
