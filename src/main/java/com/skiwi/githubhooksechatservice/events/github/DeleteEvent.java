
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 *
 * @author Frank van Heeswijk
 */
@JsonTypeInfo(use = Id.NAME, defaultImpl = DeleteEvent.class)
public final class DeleteEvent extends GithubEvent {
    @JsonProperty
    private String ref;
    
    @JsonProperty("ref_type")
    private String refType;
    
    @JsonProperty("pusher_type")
    private String pusherType;
    
    public String getRef() {
        return ref;
    }

    public String getRefType() {
        return refType;
    }

    public String getPusherType() {
        return pusherType;
    }

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + Objects.hashCode(this.ref);
		hash = 37 * hash + Objects.hashCode(this.refType);
		hash = 37 * hash + Objects.hashCode(this.pusherType);
		hash = 37 * hash + Objects.hashCode(this.repository);
		hash = 37 * hash + Objects.hashCode(this.organization);
		hash = 37 * hash + Objects.hashCode(this.sender);
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
		final DeleteEvent other = (DeleteEvent)obj;
		if (!Objects.equals(this.ref, other.ref)) {
			return false;
		}
		if (!Objects.equals(this.refType, other.refType)) {
			return false;
		}
		if (!Objects.equals(this.pusherType, other.pusherType)) {
			return false;
		}
		if (!Objects.equals(this.repository, other.repository)) {
			return false;
		}
		if (!Objects.equals(this.organization, other.organization)) {
			return false;
		}
		if (!Objects.equals(this.sender, other.sender)) {
			return false;
		}
		return true;
	}
	
	public void setPayload(DeleteEvent event) {
		this.pusherType = event.pusherType;
		this.ref = event.ref;
		this.refType = event.refType;
	}
	
}
