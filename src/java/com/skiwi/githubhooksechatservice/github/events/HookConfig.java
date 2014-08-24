
package com.skiwi.githubhooksechatservice.github.events;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class HookConfig {
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
}
