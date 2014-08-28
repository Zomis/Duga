
package com.skiwi.githubhooksechatservice.chatbot;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Frank van Heeswijk
 */
public interface ChatBot {
    void start();
    
    void stop();

	default void postMessage(final String text) {
		postMessages(text);
	}
	
	default void postMessages(final String... messages) {
		postMessages(Arrays.asList(messages));
	}
	
	void postMessages(final List<String> messages);
}
