
package com.skiwi.githubhooksechatservice.mvc.beans;

import org.springframework.beans.factory.annotation.Autowired;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.chatbot.StackExchangeChatBot;
import com.skiwi.githubhooksechatservice.mvc.configuration.Configuration;
import com.skiwi.githubhooksechatservice.store.Store;

/**
 *
 * @author Frank van Heeswijk
 */
public class StartupBean {
    @Autowired
    private Configuration configuration;
    
    public void start() {
        new Thread(() -> {
            ChatBot chatBot = new StackExchangeChatBot(configuration);
            Store.INSTANCE.setChatBot(chatBot);
            chatBot.start();
        }).start();
    }
}
