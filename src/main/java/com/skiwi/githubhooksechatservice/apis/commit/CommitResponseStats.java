
package com.skiwi.githubhooksechatservice.apis.commit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitResponseStats extends AnySetterJSONObject {
	@JsonProperty
	private long total;
	
	@JsonProperty
	private long additions;
	
	@JsonProperty
	private long deletions;

	public long getTotal() {
		return total;
	}

	public long getAdditions() {
		return additions;
	}

	public long getDeletions() {
		return deletions;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (int)(this.total ^ (this.total >>> 32));
		hash = 29 * hash + (int)(this.additions ^ (this.additions >>> 32));
		hash = 29 * hash + (int)(this.deletions ^ (this.deletions >>> 32));
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
		final CommitResponseStats other = (CommitResponseStats)obj;
		if (this.total != other.total) {
			return false;
		}
		if (this.additions != other.additions) {
			return false;
		}
		if (this.deletions != other.deletions) {
			return false;
		}
		return true;
	}
}
