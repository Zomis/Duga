package net.zomis.duga.chat.events;

import net.zomis.duga.chat.ChatBot;
import net.zomis.duga.chat.ChatMessage;

public class DugaPrepostEvent extends DugaEvent {

    private final ChatMessage chatMessage;
    private boolean performPost;
    private String message;

    public DugaPrepostEvent(ChatBot bot, ChatMessage message) {
        super(bot);
        this.chatMessage = message;
        this.message = message.getMessage();
        this.performPost = true;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPerformPost() {
        return performPost;
    }

    public void setPerformPost(boolean performPost) {
        this.performPost = performPost;
    }

}
