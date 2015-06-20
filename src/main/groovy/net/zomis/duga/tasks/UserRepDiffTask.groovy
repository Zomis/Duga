package net.zomis.duga.tasks

import net.zomis.duga.DugaBot
import net.zomis.duga.StackExchangeAPI
import net.zomis.duga.chat.WebhookParameters;

import java.util.function.ToIntFunction;

class UserRepDiffTask implements Runnable {

	private final StackExchangeAPI stackApi;
	private final DugaBot chatBot;
	private final String usersString;
	private final String site;
	private final WebhookParameters room;

	public UserRepDiffTask(StackExchangeAPI stackApi, String room, DugaBot chatBot, String users, String site) {
		this.stackApi = stackApi;
		this.chatBot = chatBot;
		this.usersString = users.replace(',', ';');
		this.room = WebhookParameters.toRoom(room);
		this.site = site;
	}

	@Override
	public void run() {
		try {
			def result = stackApi.apiCall("users/" + usersString, site, "!23IYXA.sS8.otifg5Aq.2");
			List users = result.items
			if (users.size() != 2) {
				throw new UnsupportedOperationException("Cannot check diff for anything other than two users");
			}
			
			def max = users.stream().max(Comparator.comparingInt({it.reputation})).get();
			def min = users.stream().min(Comparator.comparingInt({it.reputation})).get();
			StringBuilder str = new StringBuilder();
			str.append(clearName(max.display_name) + " vs. " + clearName(min.display_name) + ": ");
			str.append((int)max.reputation - (int)min.reputation);
			str.append(" diff. ");
			diffStr(str, max, min, "Year", {it.reputation_change_year});
			diffStr(str, max, min, "Quarter", {it.reputation_change_quarter});
			diffStr(str, max, min, "Month", {it.reputation_change_month});
			diffStr(str, max, min, "Week", {it.reputation_change_week});
			diffStr(str, max, min, "Day", {it.reputation_change_day});
			chatBot.postSingle(room, str.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String chatName(String displayName) {
		return clearName(displayName).replace(" ", "");
	}
	
	public static String clearName(String displayName) {
		while (displayName.contains("&#")) {
			String replacement = displayName.substring(displayName.indexOf("&#") + 2);
			try {
				replacement = replacement.substring(0, replacement.indexOf(';'));
				int ch = Integer.parseInt(replacement);
				displayName = displayName.replaceFirst("&#\\d+;", String.valueOf((char) ch));
			} catch (RuntimeException ex) {
				displayName = displayName.replaceFirst("&#", "");
			}
		}
		return displayName;
	}

	private void diffStr(StringBuilder str, max, min, String string, ToIntFunction<?> function) {
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
