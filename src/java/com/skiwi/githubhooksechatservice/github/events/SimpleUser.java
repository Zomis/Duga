
package com.skiwi.githubhooksechatservice.github.events;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class SimpleUser {
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
