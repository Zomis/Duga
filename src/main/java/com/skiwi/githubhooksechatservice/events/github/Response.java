
package com.skiwi.githubhooksechatservice.events.github;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public final class Response {
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

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 11 * hash + (int)(this.code ^ (this.code >>> 32));
		hash = 11 * hash + Objects.hashCode(this.status);
		hash = 11 * hash + Objects.hashCode(this.message);
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Response other = (Response)obj;
		if (this.code != other.code) {
			return false;
		}
		if (!Objects.equals(this.status, other.status)) {
			return false;
		}
		if (!Objects.equals(this.message, other.message)) {
			return false;
		}
		return true;
	}
}
