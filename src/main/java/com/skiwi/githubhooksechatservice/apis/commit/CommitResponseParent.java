
package com.skiwi.githubhooksechatservice.apis.commit;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class CommitResponseParent extends AnySetterJSONObject {
	@JsonProperty
	private String sha;
	
	@JsonProperty
	private String url;
	
	@JsonProperty("html_url")
	private String htmlUrl;

	public String getSha() {
		return sha;
	}

	public String getUrl() {
		return url;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.sha);
		hash = 67 * hash + Objects.hashCode(this.url);
		hash = 67 * hash + Objects.hashCode(this.htmlUrl);
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
		final CommitResponseParent other = (CommitResponseParent)obj;
		if (!Objects.equals(this.sha, other.sha)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		return true;
	}
}
