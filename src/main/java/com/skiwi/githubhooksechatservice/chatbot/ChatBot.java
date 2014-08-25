
package com.skiwi.githubhooksechatservice.chatbot;

/**
 *
 * @author Frank van Heeswijk
 */
public interface ChatBot {
    void start();
    
    void stop();
    
    void postMessage(final String text);
}
