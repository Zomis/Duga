
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class Hook {
    @JsonProperty
    private String url;
    
    @JsonProperty("test_url")
    private String testUrl;
    
    @JsonProperty
    private long id;
    
    @JsonProperty
    private String name;
    
    @JsonProperty
    private boolean active;
    
    @JsonProperty
    private String[] events;
    
    @JsonProperty
    private HookConfig config;
    
    @JsonProperty("last_response")
    private Response lastResponse;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("created_at")
    private String createdAt;

    public String getUrl() {
        return url;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<String> getEvents() {
        return Arrays.asList(events);
    }

    public HookConfig getConfig() {
        return config;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
