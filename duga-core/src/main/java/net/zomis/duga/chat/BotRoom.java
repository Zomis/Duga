package net.zomis.duga.chat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BotRoom {
	
	private String roomId;

	public String getRoomId() {
		return roomId;
	}
	
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public static BotRoom toRoom(String roomId) {
		BotRoom params = new BotRoom();
		params.setRoomId(roomId);
		return params;
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
		return new ChatMessage(this, input);
	}

    public List<ChatMessage> messages(String... messages) {
        return Arrays.stream(messages).map(this::message).collect(Collectors.toList());
    }

    public List<ChatMessage> messages(List<String> messages) {
        return messages.stream().map(this::message).collect(Collectors.toList());
    }

}
