package com.skiwi.githubhooksechatservice.mvc.beans.tasks;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.mvc.beans.StackExchangeAPIBean;
import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;
import com.skiwi.githubhooksechatservice.stackapi.StackUser;
import com.skiwi.githubhooksechatservice.stackapi.StackUsers;

public class UserRepDiffTask implements Runnable {

	private final StackExchangeAPIBean stackApi;
	private final ChatBot chatBot;
	private final String usersString;
	private final String site;
	private final WebhookParameters room;

	public UserRepDiffTask(StackExchangeAPIBean stackApi, String room, ChatBot chatBot, String users, String site) {
		this.stackApi = stackApi;
		this.chatBot = chatBot;
		this.usersString = users.replace(',', ';');
		this.room = WebhookParameters.toRoom(room);
		this.site = site;
	}

	@Override
	public void run() {
		try {
			StackUsers result = stackApi.apiCall("users/" + usersString, site, "!23IYXA.sS8.otifg5Aq.2", StackUsers.class);
			List<StackUser> users = result.getItems();
			if (users.size() != 2) {
				throw new UnsupportedOperationException("Cannot check diff for anything other than two users");
			}
			
			StackUser max = users.stream().max(Comparator.comparingInt(StackUser::getReputation)).get();
			StackUser min = users.stream().min(Comparator.comparingInt(StackUser::getReputation)).get();
			StringBuilder str = new StringBuilder();
			str.append(clearName(max.getDisplayName()) + " vs. " + clearName(min.getDisplayName()) + ": ");
			str.append(max.getReputation() - min.getReputation());
			str.append(" diff. ");
			diffStr(str, max, min, "Year", StackUser::getReputationChangeYear);
			diffStr(str, max, min, "Quarter", StackUser::getReputationChangeQuarter);
			diffStr(str, max, min, "Month", StackUser::getReputationChangeMonth);
			diffStr(str, max, min, "Week", StackUser::getReputationChangeWeek);
			diffStr(str, max, min, "Day", StackUser::getReputationChangeDay);
			chatBot.postMessage(room, str.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String clearName(String displayName) {
		while (displayName.contains("&#")) {
			String replacement = displayName.substring(displayName.indexOf("&#"));
			replacement = replacement.substring(0, replacement.indexOf(';'));
			displayName = displayName.replaceFirst("\\&\\#\\d+\\;", replacement);
		}
		return displayName;
	}

	private void diffStr(StringBuilder str, StackUser max, StackUser min,
			String string, ToIntFunction<StackUser> function) {
		str.append(string);
		str.append(": ");
		int maxValue = function.applyAsInt(max);
		int minValue = function.applyAsInt(min);
		int diff = maxValue - minValue;
		str.append(diff > 0 ? "+" : "");
		str.append(diff);
		str.append(". ");
	}
	
}
