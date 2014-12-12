package com.skiwi.githubhooksechatservice.chatbot;

import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;

public class ChatMessage {
	
	private final String room;
	private final String message;

	public ChatMessage(WebhookParameters params, String message) {
		this.room = params.getRoom();
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getRoom() {
		return room;
	}
	
}
