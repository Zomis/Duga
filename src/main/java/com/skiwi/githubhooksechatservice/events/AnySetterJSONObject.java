
package com.skiwi.githubhooksechatservice.events;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.skiwi.githubhooksechatservice.jackson.MessageTypeIdResolver;

/**
 *
 * @author Frank van Heeswijk
 */
@JsonTypeInfo(use = Id.CUSTOM, property = "type", include = As.PROPERTY)
@JsonTypeIdResolver(MessageTypeIdResolver.class)
public abstract class AnySetterJSONObject {
	@JsonAnySetter
	protected void anySetter(final String name, final Object value) {
		System.out.println("Could not set property " + name + " in " + getClass().getName() + ".");
	}
}
