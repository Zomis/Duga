package com.skiwi.githubhooksechatservice.mvc.controllers;

import com.skiwi.githubhooksechatservice.mvc.configuration.Configuration;

public class WebhookParameters {
	
	private String room;
	
	public String getRoom() {
		return room;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}

	public void init(Configuration configuration) {
		if (room == null) {
			room = configuration.getRoomId();
		}
	}
	
}
