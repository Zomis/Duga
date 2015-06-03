package com.skiwi.githubhooksechatservice.mvc.beans.tasks;

import java.time.Instant;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;

public class MessageTask implements Runnable {

	private final ChatBot chatBot;
	private final WebhookParameters room;
	private final String message;

	public MessageTask(ChatBot chatBot, String room, String message) {
		this.chatBot = chatBot;
		this.room = WebhookParameters.toRoom(room);
		this.message = message;
	}

	@Override
	public void run() {
		chatBot.postMessage(room, message.replace("%time%", Instant.now().toString()));
	}

	@Override
	public String toString() {
		return "MessageTask [chatBot=" + chatBot + ", room=" + room
				+ ", message=" + message + "]";
	}
		
}
