
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class LegacyCommit {
    @JsonProperty
    private String id;
    
    @JsonProperty
    private boolean distinct;
    
    @JsonProperty
    private String message;
    
    @JsonProperty
    private String timestamp;
    
    @JsonProperty
    private String url;
    
    @JsonProperty
    private LegacyUser author;
    
    @JsonProperty
    private LegacyUser committer;
    
    @JsonProperty
    private String[] added;
    
    @JsonProperty
    private String[] removed;
    
    @JsonProperty
    private String[] modified;

    public String getId() {
        return id;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }

    public LegacyUser getAuthor() {
        return author;
    }

    public LegacyUser getCommitter() {
        return committer;
    }

    public List<String> getAdded() {
        return Arrays.asList(added);
    }

    public List<String> getRemoved() {
        return Arrays.asList(removed);
    }

    public List<String> getModified() {
        return Arrays.asList(modified);
    }

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.id);
		hash = 97 * hash + (this.distinct ? 1 : 0);
		hash = 97 * hash + Objects.hashCode(this.message);
		hash = 97 * hash + Objects.hashCode(this.timestamp);
		hash = 97 * hash + Objects.hashCode(this.url);
		hash = 97 * hash + Objects.hashCode(this.author);
		hash = 97 * hash + Objects.hashCode(this.committer);
		hash = 97 * hash + Arrays.deepHashCode(this.added);
		hash = 97 * hash + Arrays.deepHashCode(this.removed);
		hash = 97 * hash + Arrays.deepHashCode(this.modified);
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
		final LegacyCommit other = (LegacyCommit)obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		if (this.distinct != other.distinct) {
			return false;
		}
		if (!Objects.equals(this.message, other.message)) {
			return false;
		}
		if (!Objects.equals(this.timestamp, other.timestamp)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.author, other.author)) {
			return false;
		}
		if (!Objects.equals(this.committer, other.committer)) {
			return false;
		}
		if (!Arrays.deepEquals(this.added, other.added)) {
			return false;
		}
		if (!Arrays.deepEquals(this.removed, other.removed)) {
			return false;
		}
		if (!Arrays.deepEquals(this.modified, other.modified)) {
			return false;
		}
		return true;
	}
}
