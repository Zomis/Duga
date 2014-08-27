
package com.skiwi.githubhooksechatservice.mvc.beans;

import org.springframework.beans.factory.annotation.Autowired;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.mvc.configuration.Configuration;

/**
 *
 * @author Frank van Heeswijk
 */
public class StartupBean {
    @Autowired
    private Configuration configuration;
    
    @Autowired
    private ChatBot chatBot;
    
    public void start() {
        new Thread(chatBot::start).start();
    }
}
