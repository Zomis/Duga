
package com.skiwi.githubhooksechatservice.store;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import java.util.Objects;

/**
 *
 * @author Frank van Heeswijk
 */
public enum Store {
    INSTANCE;
    
    private ChatBot chatBot;
    
    public void setChatBot(final ChatBot chatBot) {
        this.chatBot = Objects.requireNonNull(chatBot, "chatBot");
    }
    
    public ChatBot getChatBot() {
        return chatBot;
    }
}
