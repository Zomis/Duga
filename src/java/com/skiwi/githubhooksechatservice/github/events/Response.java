
package com.skiwi.githubhooksechatservice.github.events;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class Response {
    @JsonProperty
    private long code;
    
    @JsonProperty
    private String status;
    
    @JsonProperty
    private String message;

    public long getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
