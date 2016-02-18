package net.zomis.duga.chat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BotRoom {

    private final ChatBot bot;
    private String roomId;

    public BotRoom(ChatBot bot, String roomId) {
        this.bot = bot;
        this.roomId = roomId;
    }

    public String getRoomId() {
		return roomId;
	}

    @Deprecated
	public static BotRoom toRoom(String roomId) {
		return new BotRoom(null, roomId);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
		if (o == null) return false;
        if (getClass() != o.getClass()) return false;

        BotRoom that = (BotRoom) o;

        if (!roomId.equals(that.roomId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roomId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Room " + roomId;
    }

	public ChatMessage message(String input) {
		return new ChatMessage(bot, this, input);
	}

    public List<ChatMessage> messages(String... messages) {
        return Arrays.stream(messages).map(this::message).collect(Collectors.toList());
    }

    public List<ChatMessage> messages(List<String> messages) {
        return messages.stream().map(this::message).collect(Collectors.toList());
    }

}
