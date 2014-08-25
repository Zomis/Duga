
package com.skiwi.githubhooksechatservice.github.events;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class DeleteEvent {
    @JsonProperty
    private String ref;
    
    @JsonProperty("ref_type")
    private String refType;
    
    @JsonProperty("pusher_type")
    private String pusherType;
    
    @JsonProperty
    private Repository repository;
    
    @JsonProperty
    private Account sender;

    public String getRef() {
        return ref;
    }

    public String getRefType() {
        return refType;
    }

    public String getPusherType() {
        return pusherType;
    }

    public Repository getRepository() {
        return repository;
    }

    public Account getSender() {
        return sender;
    }
}
