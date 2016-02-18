package net.zomis.duga.tasks

import net.zomis.duga.chat.BotRoom;

import java.time.Instant;

import net.zomis.duga.DugaBotService;

public class MessageTask implements Runnable {

	private final DugaBotService chatBot;
	private final BotRoom room;
	private final String message;

	public MessageTask(DugaBotService chatBot, String room, String message) {
		this.chatBot = chatBot;
		this.room = chatBot.room(room);
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
