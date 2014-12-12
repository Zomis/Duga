
package com.skiwi.githubhooksechatservice.chatbot;

import java.util.Arrays;
import java.util.List;

import com.skiwi.githubhooksechatservice.mvc.controllers.WebhookParameters;

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
	
	default void postMessage(final WebhookParameters params, final String text) {
		postMessages(params, text);
	}
	
	default void postMessages(final WebhookParameters params, final String... messages) {
		postMessages(params, Arrays.asList(messages));
	}
	
	default void postMessages(final List<String> messages) {
		postMessages(null, messages);
	}
	
	void postMessages(WebhookParameters params, final List<String> messages);
}
