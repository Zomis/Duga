
package com.skiwi.githubhooksechatservice.events.github.classes;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.skiwi.githubhooksechatservice.events.github.GithubEvent;

/**
 *
 * @author Frank van Heeswijk
 */
@JsonTypeInfo(use = Id.NAME, defaultImpl = PingEvent.class)
public final class PingEvent extends GithubEvent {
    @JsonProperty
    private String zen;
    
    @JsonProperty
    private Hook hook;
    
    @JsonProperty("hook_id")
    private long hookId;

    public String getZen() {
        return zen;
    }

    public Hook getHook() {
        return hook;
    }

    public long getHookId() {
        return hookId;
    }

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.zen);
		hash = 59 * hash + Objects.hashCode(this.hook);
		hash = 59 * hash + (int)(this.hookId ^ (this.hookId >>> 32));
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
		final PingEvent other = (PingEvent)obj;
		if (!Objects.equals(this.zen, other.zen)) {
			return false;
		}
		if (!Objects.equals(this.hook, other.hook)) {
			return false;
		}
		if (this.hookId != other.hookId) {
			return false;
		}
		return true;
	}
}
