
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class PushEvent {
    @JsonProperty
    private String ref;
    
    @JsonProperty
    private String after;
    
    @JsonProperty
    private String before;
    
    @JsonProperty
    private boolean created;
    
    @JsonProperty
    private boolean deleted;
    
    @JsonProperty
    private boolean forced;
	
	@JsonProperty(value = "base_ref", required = false)
	private String baseRef;
    
    @JsonProperty
    private String compare;
    
    @JsonProperty
    private LegacyCommit[] commits;
    
    @JsonProperty("head_commit")
    private LegacyCommit headCommit;
    
    @JsonProperty
    private LegacyRepository repository;
    
    @JsonProperty
    private LegacySimpleUser pusher;

    public String getRef() {
        return ref;
    }

    public String getAfter() {
        return after;
    }

    public String getBefore() {
        return before;
    }

    public boolean isCreated() {
        return created;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isForced() {
        return forced;
    }

	public String getBaseRef() {
		return baseRef;
	}

    public String getCompare() {
        return compare;
    }

    public List<LegacyCommit> getCommits() {
        return Arrays.asList(commits);
    }

    public LegacyCommit getHeadCommit() {
        return headCommit;
    }

    public LegacyRepository getRepository() {
        return repository;
    }

    public LegacySimpleUser getPusher() {
        return pusher;
    }

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + Objects.hashCode(this.ref);
		hash = 23 * hash + Objects.hashCode(this.after);
		hash = 23 * hash + Objects.hashCode(this.before);
		hash = 23 * hash + (this.created ? 1 : 0);
		hash = 23 * hash + (this.deleted ? 1 : 0);
		hash = 23 * hash + (this.forced ? 1 : 0);
		hash = 23 * hash + Objects.hashCode(this.baseRef);
		hash = 23 * hash + Objects.hashCode(this.compare);
		hash = 23 * hash + Arrays.deepHashCode(this.commits);
		hash = 23 * hash + Objects.hashCode(this.headCommit);
		hash = 23 * hash + Objects.hashCode(this.repository);
		hash = 23 * hash + Objects.hashCode(this.pusher);
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
		final PushEvent other = (PushEvent)obj;
		if (!Objects.equals(this.ref, other.ref)) {
			return false;
		}
		if (!Objects.equals(this.after, other.after)) {
			return false;
		}
		if (!Objects.equals(this.before, other.before)) {
			return false;
		}
		if (this.created != other.created) {
			return false;
		}
		if (this.deleted != other.deleted) {
			return false;
		}
		if (this.forced != other.forced) {
			return false;
		}
		if (!Objects.equals(this.baseRef, other.baseRef)) {
			return false;
		}
		if (!Objects.equals(this.compare, other.compare)) {
			return false;
		}
		if (!Arrays.deepEquals(this.commits, other.commits)) {
			return false;
		}
		if (!Objects.equals(this.headCommit, other.headCommit)) {
			return false;
		}
		if (!Objects.equals(this.repository, other.repository)) {
			return false;
		}
		if (!Objects.equals(this.pusher, other.pusher)) {
			return false;
		}
		return true;
	}
}
