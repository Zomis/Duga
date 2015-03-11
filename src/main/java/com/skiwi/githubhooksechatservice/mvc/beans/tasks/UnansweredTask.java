package com.skiwi.githubhooksechatservice.mvc.beans.tasks;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.mvc.beans.StackExchangeAPIBean;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;

public class UnansweredTask implements Runnable {
	private static final Logger logger = LogManager.getLogger(UnansweredTask.class);
	
	private final StackExchangeAPIBean api;
	private final WebhookParameters room;
	private final ChatBot bot;
	private final String site;
	private final String message;

	public UnansweredTask(StackExchangeAPIBean stackAPI, String room,
		ChatBot chatBot, String site, String message) {
		this.api = stackAPI;
		this.room = WebhookParameters.toRoom(room);
		this.bot = chatBot;
		this.site = site;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			JsonNode result = api.apiCall("info", site, "default");
			int unanswered = result.get("items").get(0).get("total_unanswered").asInt();
			bot.postMessage(room, message.replace("%unanswered%", String.valueOf(unanswered)));
		} catch (IOException e) {
			logger.error("Error with StackExchange API Call", e);
		}
	}

}
