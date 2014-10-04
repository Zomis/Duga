
package com.skiwi.githubhooksechatservice.apis.commit;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitResponseCommitTree {
	@JsonProperty
	private String sha;
	
	@JsonProperty
	private String url;

	public String getSha() {
		return sha;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 11 * hash + Objects.hashCode(this.sha);
		hash = 11 * hash + Objects.hashCode(this.url);
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
		final CommitResponseCommitTree other = (CommitResponseCommitTree)obj;
		if (!Objects.equals(this.sha, other.sha)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		return true;
	}
}
