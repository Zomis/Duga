package com.skiwi.githubhooksechatservice.stackapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StackUser {
	
	@JsonProperty("account_id")
	private int accountId;
	
	@JsonProperty("reputation_change_year")
	private int reputationChangeYear;
	
	@JsonProperty("reputation_change_quarter")
	private int reputationChangeQuarter;
	
	@JsonProperty("reputation_change_month")
	private int reputationChangeMonth;
	
	@JsonProperty("reputation_change_week")
	private int reputationChangeWeek;
	
	@JsonProperty("reputation_change_day")
	private int reputationChangeDay;
	
	@JsonProperty("reputation")
	private int reputation;
	
	@JsonProperty("user_id")
	private int userId;
	
	@JsonProperty("display_name")
	private String displayName;
	
	public int getAccountId() {
		return accountId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getReputation() {
		return reputation;
	}
	
	public int getReputationChangeDay() {
		return reputationChangeDay;
	}
	
	public int getReputationChangeMonth() {
		return reputationChangeMonth;
	}
	
	public int getReputationChangeQuarter() {
		return reputationChangeQuarter;
	}
	
	public int getReputationChangeWeek() {
		return reputationChangeWeek;
	}
	
	public int getReputationChangeYear() {
		return reputationChangeYear;
	}
	
	public int getUserId() {
		return userId;
	}
	
}
