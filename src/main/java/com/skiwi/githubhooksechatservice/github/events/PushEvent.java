
package com.skiwi.githubhooksechatservice.github.events;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class PushEvent {
    @JsonProperty
    private String ref;
    
    @JsonProperty
    private String after;
    
    @JsonProperty
    private String before;
    
    @JsonProperty
    private boolean created;
    
    @JsonProperty
    private boolean deleted;
    
    @JsonProperty
    private boolean forced;
	
	@JsonProperty(value = "base_ref", required = false)
	private String baseRef;
    
    @JsonProperty
    private String compare;
    
    @JsonProperty
    private LegacyCommit[] commits;
    
    @JsonProperty("head_commit")
    private LegacyCommit headCommit;
    
    @JsonProperty
    private LegacyRepository repository;
    
    @JsonProperty
    private LegacySimpleUser pusher;

    public String getRef() {
        return ref;
    }

    public String getAfter() {
        return after;
    }

    public String getBefore() {
        return before;
    }

    public boolean isCreated() {
        return created;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isForced() {
        return forced;
    }

	public String getBaseRef() {
		return baseRef;
	}

    public String getCompare() {
        return compare;
    }

    public List<LegacyCommit> getCommits() {
        return Arrays.asList(commits);
    }

    public LegacyCommit getHeadCommit() {
        return headCommit;
    }

    public LegacyRepository getRepository() {
        return repository;
    }

    public LegacySimpleUser getPusher() {
        return pusher;
    }
}
