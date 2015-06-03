package com.skiwi.githubhooksechatservice.stackapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StackResult {
	
	@JsonProperty("has_more")
	private boolean hasMore;
	
	@JsonProperty("quota_max")
	private int quotaMax;
	
	@JsonProperty("quota_remaining")
	private int quotaRemaining;
	
	@JsonProperty
	private int backoff;
	
	@JsonProperty("error_id")
	private int errorId;
	
	@JsonProperty("error_message")
	private String errorMessage;
	
	@JsonProperty("error_name")
	private String errorName;
	
	public int getBackoff() {
		return backoff;
	}
	
	public int getErrorId() {
		return errorId;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getErrorName() {
		return errorName;
	}

	public int getQuotaMax() {
		return quotaMax;
	}
	
	public int getQuotaRemaining() {
		return quotaRemaining;
	}
	
}
