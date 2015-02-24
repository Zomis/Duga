
package com.skiwi.githubhooksechatservice.events.github.classes;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class HookConfig extends AnySetterJSONObject {
    @JsonProperty
    private String secret;
    
    @JsonProperty
    private String url;
    
    @JsonProperty("content_type")
    private String contentType;
    
    @JsonProperty("insecure_ssl")
    private String insecureSsl;

    public String getSecret() {
        return secret;
    }

    public String getUrl() {
        return url;
    }

    public String getContentType() {
        return contentType;
    }

    public String getInsecureSsl() {
        return insecureSsl;
    }

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.secret);
		hash = 41 * hash + Objects.hashCode(this.url);
		hash = 41 * hash + Objects.hashCode(this.contentType);
		hash = 41 * hash + Objects.hashCode(this.insecureSsl);
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
		final HookConfig other = (HookConfig)obj;
		if (!Objects.equals(this.secret, other.secret)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.contentType, other.contentType)) {
			return false;
		}
		if (!Objects.equals(this.insecureSsl, other.insecureSsl)) {
			return false;
		}
		return true;
	}
}
