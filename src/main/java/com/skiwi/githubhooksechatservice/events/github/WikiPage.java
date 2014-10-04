
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class WikiPage extends AnySetterJSONObject {
	@JsonProperty("page_name")
	private String pageName;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private String summary;
	
	@JsonProperty
	private String action;
	
	@JsonProperty
	private String sha;
	
	@JsonProperty("html_url")
	private String htmlUrl;

	public String getPageName() {
		return pageName;
	}

	public String getTitle() {
		return title;
	}

	public String getSummary() {
		return summary;
	}

	public String getAction() {
		return action;
	}

	public String getSha() {
		return sha;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 37 * hash + Objects.hashCode(this.pageName);
		hash = 37 * hash + Objects.hashCode(this.title);
		hash = 37 * hash + Objects.hashCode(this.summary);
		hash = 37 * hash + Objects.hashCode(this.action);
		hash = 37 * hash + Objects.hashCode(this.sha);
		hash = 37 * hash + Objects.hashCode(this.htmlUrl);
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
		final WikiPage other = (WikiPage)obj;
		if (!Objects.equals(this.pageName, other.pageName)) {
			return false;
		}
		if (!Objects.equals(this.title, other.title)) {
			return false;
		}
		if (!Objects.equals(this.summary, other.summary)) {
			return false;
		}
		if (!Objects.equals(this.action, other.action)) {
			return false;
		}
		if (!Objects.equals(this.sha, other.sha)) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		return true;
	}
}
