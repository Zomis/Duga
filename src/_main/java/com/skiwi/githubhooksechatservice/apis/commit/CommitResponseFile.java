
package com.skiwi.githubhooksechatservice.apis.commit;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitResponseFile extends AnySetterJSONObject {
	@JsonProperty
	private String sha;
	
	@JsonProperty
	private String filename;
	
	@JsonProperty
	private String status;
	
	@JsonProperty
	private long additions;
	
	@JsonProperty
	private long deletions;
	
	@JsonProperty
	private long changes;
	
	@JsonProperty("blob_url")
	private String blobUrl;
	
	@JsonProperty("raw_url")
	private String rawUrl;
	
	@JsonProperty("contents_url")
	private String contentsUrl;
	
	@JsonProperty
	private String patch;

	public String getSha() {
		return sha;
	}

	public String getFilename() {
		return filename;
	}

	public String getStatus() {
		return status;
	}

	public long getAdditions() {
		return additions;
	}

	public long getDeletions() {
		return deletions;
	}

	public long getChanges() {
		return changes;
	}

	public String getBlobUrl() {
		return blobUrl;
	}

	public String getRawUrl() {
		return rawUrl;
	}

	public String getContentsUrl() {
		return contentsUrl;
	}

	public String getPatch() {
		return patch;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 23 * hash + Objects.hashCode(this.sha);
		hash = 23 * hash + Objects.hashCode(this.filename);
		hash = 23 * hash + Objects.hashCode(this.status);
		hash = 23 * hash + (int)(this.additions ^ (this.additions >>> 32));
		hash = 23 * hash + (int)(this.deletions ^ (this.deletions >>> 32));
		hash = 23 * hash + (int)(this.changes ^ (this.changes >>> 32));
		hash = 23 * hash + Objects.hashCode(this.blobUrl);
		hash = 23 * hash + Objects.hashCode(this.rawUrl);
		hash = 23 * hash + Objects.hashCode(this.contentsUrl);
		hash = 23 * hash + Objects.hashCode(this.patch);
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
		final CommitResponseFile other = (CommitResponseFile)obj;
		if (!Objects.equals(this.sha, other.sha)) {
			return false;
		}
		if (!Objects.equals(this.filename, other.filename)) {
			return false;
		}
		if (!Objects.equals(this.status, other.status)) {
			return false;
		}
		if (this.additions != other.additions) {
			return false;
		}
		if (this.deletions != other.deletions) {
			return false;
		}
		if (this.changes != other.changes) {
			return false;
		}
		if (!Objects.equals(this.blobUrl, other.blobUrl)) {
			return false;
		}
		if (!Objects.equals(this.rawUrl, other.rawUrl)) {
			return false;
		}
		if (!Objects.equals(this.contentsUrl, other.contentsUrl)) {
			return false;
		}
		if (!Objects.equals(this.patch, other.patch)) {
			return false;
		}
		return true;
	}
}
