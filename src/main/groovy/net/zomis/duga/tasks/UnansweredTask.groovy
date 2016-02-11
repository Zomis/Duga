package net.zomis.duga.tasks

import net.zomis.duga.DugaBotService;

import net.zomis.duga.StackExchangeAPI;
import net.zomis.duga.chat.WebhookParameters;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UnansweredTask implements Runnable {
	private static final Logger logger = LogManager.getLogger(UnansweredTask.class);
	
	private final StackExchangeAPI api;
	private final WebhookParameters room;
	private final DugaBotService bot;
	private final String site;
	private final String message;

	public UnansweredTask(StackExchangeAPI stackAPI, String room,
						  DugaBotService chatBot, String site, String message) {
		this.api = stackAPI;
		this.room = WebhookParameters.toRoom(room);
		this.bot = chatBot;
		this.site = site;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			def result = api.apiCall("info", site, "default");
			int unanswered = result.items[0].total_unanswered as int;
			int total = result.items[0].total_questions as int;
			String message = this.message;
			double percentageAnswered = (double) (total - unanswered) / total;
			String percentageStr = String.format("%.4f", percentageAnswered * 100);
			message = message.replace("%unanswered%", String.valueOf(unanswered));
			message = message.replace("%percentage%", String.valueOf(percentageStr));
			bot.postSingle(room, message);
		} catch (IOException e) {
			logger.error("Error with StackExchange API Call", e);
		}
	}

}
