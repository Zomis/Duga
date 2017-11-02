package net.zomis.duga.chat;

import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.gistlabs.mechanize.document.json.JsonDocument;

public class ChatMessage {
	
	private final String room;
	private final String message;
	private Consumer<JsonDocument> onSuccess;
    private final ChatBot bot;
    private final BotRoom params;

    public ChatMessage(BotRoom params, String message) {
        this(null, params, message);
    }

    public ChatMessage(ChatBot bot, BotRoom params, String message) {
        this.bot = bot;
        this.params = params;
        this.room = params.getRoomId();
        this.message = message;
        this.onSuccess = null;
    }

    public ChatMessage(BotRoom params, String message, Consumer<JsonDocument> onSuccess) {
        this(null, params, message);
		this.onSuccess = onSuccess;
	}

    public ChatMessage createCopy(String text) {
        return new ChatMessage(bot, params, text);
    }

    public Future<ChatMessageResponse> post() {
        return bot.postAsync(this);
    }

    public ChatMessageResponse postNow() {
        return bot.postNow(this);
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
	
    @Override
    public String toString() {
        return "ChatMessage{" +
                "room='" + room + '\'' +
                ", message='" + message + '\'' +
                ", params=" + params +
                '}';
    }
}
