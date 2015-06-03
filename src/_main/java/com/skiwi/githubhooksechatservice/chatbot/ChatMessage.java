package com.skiwi.githubhooksechatservice.chatbot;

import java.util.function.Consumer;

import com.gistlabs.mechanize.document.json.JsonDocument;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;

public class ChatMessage {
	
	private final String room;
	private final String message;
	private final Consumer<JsonDocument> onSuccess;

	public ChatMessage(WebhookParameters params, String message) {
		this(params, message, null);
	}
	
	public ChatMessage(WebhookParameters params, String message, Consumer<JsonDocument> onSuccess) {
		this.room = params.getRoomId();
		this.message = message;
		this.onSuccess = onSuccess;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getRoom() {
		return room;
	}

	void onSuccess(JsonDocument response) {
		if (onSuccess != null) {
			onSuccess.accept(response);
		}
	}
	
}
