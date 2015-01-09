package com.skiwi.githubhooksechatservice.mvc.controllers;


public class WebhookParameters {
	
	private String roomId;
	
	public String getRoomId() {
		return roomId;
	}
	
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public void useDefaultRoom(String defaultRoomId) {
		if (roomId == null) {
			roomId = defaultRoomId;
		}
	}
	
}
