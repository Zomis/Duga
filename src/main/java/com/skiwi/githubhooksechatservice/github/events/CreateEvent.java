
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class CreateEvent {
    @JsonProperty
    private String ref;
    
    @JsonProperty("ref_type")
    private String refType;
    
    @JsonProperty("master_branch")
    private String masterBranch;
    
    @JsonProperty
    private String description;
    
    @JsonProperty("pusher_type")
    private String pusherType;
    
    @JsonProperty
    private Repository repository;
    
    @JsonProperty
    private User sender;

    public String getRef() {
        return ref;
    }

    public String getRefType() {
        return refType;
    }

    public String getMasterBranch() {
        return masterBranch;
    }

    public String getDescription() {
        return description;
    }

    public String getPusherType() {
        return pusherType;
    }

    public Repository getRepository() {
        return repository;
    }

    public User getSender() {
        return sender;
    }
}
