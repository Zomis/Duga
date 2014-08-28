
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CreateEvent {
    @JsonProperty
    private String ref;
    
    @JsonProperty("ref_type")
    private String refType;
    
    @JsonProperty("master_branch")
    private String masterBranch;
    
    @JsonProperty
    private String description;
    
    @JsonProperty("pusher_type")
    private String pusherType;
    
    @JsonProperty
    private Repository repository;
    
    @JsonProperty
    private User sender;

    public String getRef() {
        return ref;
    }

    public String getRefType() {
        return refType;
    }

    public String getMasterBranch() {
        return masterBranch;
    }

    public String getDescription() {
        return description;
    }

    public String getPusherType() {
        return pusherType;
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
		hash = 23 * hash + Objects.hashCode(this.ref);
		hash = 23 * hash + Objects.hashCode(this.refType);
		hash = 23 * hash + Objects.hashCode(this.masterBranch);
		hash = 23 * hash + Objects.hashCode(this.description);
		hash = 23 * hash + Objects.hashCode(this.pusherType);
		hash = 23 * hash + Objects.hashCode(this.repository);
		hash = 23 * hash + Objects.hashCode(this.sender);
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
		final CreateEvent other = (CreateEvent)obj;
		if (!Objects.equals(this.ref, other.ref)) {
			return false;
		}
		if (!Objects.equals(this.refType, other.refType)) {
			return false;
		}
		if (!Objects.equals(this.masterBranch, other.masterBranch)) {
			return false;
		}
		if (!Objects.equals(this.description, other.description)) {
			return false;
		}
		if (!Objects.equals(this.pusherType, other.pusherType)) {
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
