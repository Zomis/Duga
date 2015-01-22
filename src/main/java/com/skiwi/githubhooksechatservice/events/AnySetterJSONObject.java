
package com.skiwi.githubhooksechatservice.events;

import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 *
 * @author Frank van Heeswijk
 */
public abstract class AnySetterJSONObject {
	@JsonAnySetter
	protected void anySetter(final String name, final Object value) {
		System.out.println("Could not set property " + name + " in " + getClass().getName() + ".");
	}
}
