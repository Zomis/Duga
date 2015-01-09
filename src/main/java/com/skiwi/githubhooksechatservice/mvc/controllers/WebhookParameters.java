package com.skiwi.githubhooksechatservice.mvc.controllers;

import com.skiwi.githubhooksechatservice.mvc.configuration.Configuration;

public class WebhookParameters {
	
	private String roomId;
	
	public String getRoomId() {
		return roomId;
	}
	
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public void init(Configuration configuration) {
		if (roomId == null) {
			roomId = configuration.getRoomId();
		}
	}
	
}
