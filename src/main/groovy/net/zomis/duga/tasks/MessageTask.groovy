package net.zomis.duga.tasks;

import java.time.Instant;

import net.zomis.duga.DugaBot;
import net.zomis.duga.chat.WebhookParameters;

public class MessageTask implements Runnable {

	private final DugaBot chatBot;
	private final WebhookParameters room;
	private final String message;

	public MessageTask(DugaBot chatBot, String room, String message) {
		this.chatBot = chatBot;
		this.room = WebhookParameters.toRoom(room);
		this.message = message;
	}

	@Override
	public void run() {
		chatBot.postSingle(room, message.replace("%time%", Instant.now().toString()));
	}

	@Override
	public String toString() {
		return "MessageTask [chatBot=" + chatBot + ", room=" + room
				+ ", message=" + message + "]";
	}
		
}
