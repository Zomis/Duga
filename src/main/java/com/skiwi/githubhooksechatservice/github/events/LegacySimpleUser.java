
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class LegacySimpleUser {
	@JsonProperty
	private String name;
	
	@JsonProperty
	private String email;

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
