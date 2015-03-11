package com.skiwi.githubhooksechatservice.stackapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StackComments extends StackResult {
	
	@JsonProperty
	private List<StackExchangeComment> items;
	
	public List<StackExchangeComment> getItems() {
		return items;
	}
	
}
