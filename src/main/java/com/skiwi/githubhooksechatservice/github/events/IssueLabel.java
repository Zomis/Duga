
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class IssueLabel {
	@JsonProperty
	private String url;
	
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String color;

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}
}
