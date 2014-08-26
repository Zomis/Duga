
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class LegacyCommit {
    @JsonProperty
    private String id;
    
    @JsonProperty
    private boolean distinct;
    
    @JsonProperty
    private String message;
    
    @JsonProperty
    private String timestamp;
    
    @JsonProperty
    private String url;
    
    @JsonProperty
    private LegacyUser author;
    
    @JsonProperty
    private LegacyUser committer;
    
    @JsonProperty
    private String[] added;
    
    @JsonProperty
    private String[] removed;
    
    @JsonProperty
    private String[] modified;

    public String getId() {
        return id;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }

    public LegacyUser getAuthor() {
        return author;
    }

    public LegacyUser getCommitter() {
        return committer;
    }

    public List<String> getAdded() {
        return Arrays.asList(added);
    }

    public List<String> getRemoved() {
        return Arrays.asList(removed);
    }

    public List<String> getModified() {
        return Arrays.asList(modified);
    }
}
