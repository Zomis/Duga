package com.skiwi.githubhooksechatservice.mvc.controllers;


public class WebhookParameters {
	
	private String roomId;
	private Boolean post;
	
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
	
	public boolean getPost() {
		return post == null ? true : post;
	}
	
	public void setPost(Boolean post) {
		this.post = post;
	}
	
	public static WebhookParameters toRoom(String roomId) {
		WebhookParameters params = new WebhookParameters();
		params.setPost(true);
		params.setRoomId(roomId);
		return params;
	}
	
}
