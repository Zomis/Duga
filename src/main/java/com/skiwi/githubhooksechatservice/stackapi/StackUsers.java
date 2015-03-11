package com.skiwi.githubhooksechatservice.stackapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class StackUsers extends StackResult {
	
	@JsonProperty
	private List<StackUser> items;
	
	public List<StackUser> getItems() {
		return items;
	}
	
}
