
package com.skiwi.githubhooksechatservice.events.github;

import com.skiwi.githubhooksechatservice.events.BaseEvent;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class SimplePullRequest extends BaseEvent {
	@JsonProperty
	private String url;
	
	@JsonProperty("html_url")
	private String htmlUrl;
	
	@JsonProperty("diff_url")
	private String diffUrl;
	
	@JsonProperty("patch_url")
	private String patchUrl;

	public String getUrl() {
		return url;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public String getDiffUrl() {
		return diffUrl;
	}

	public String getPatchUrl() {
		return patchUrl;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.url);
		hash = 83 * hash + Objects.hashCode(this.htmlUrl);
		hash = 83 * hash + Objects.hashCode(this.diffUrl);
		hash = 83 * hash + Objects.hashCode(this.patchUrl);
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
		final SimplePullRequest other = (SimplePullRequest)obj;
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (!Objects.equals(this.diffUrl, other.diffUrl)) {
			return false;
		}
		if (!Objects.equals(this.patchUrl, other.patchUrl)) {
			return false;
		}
		return true;
	}
}
