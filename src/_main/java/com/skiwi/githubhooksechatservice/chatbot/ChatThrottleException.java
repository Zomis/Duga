
package com.skiwi.githubhooksechatservice.chatbot;

/**
 *
 * @author Frank van Heeswijk
 */
public class ChatThrottleException extends Exception {
	private static final long serialVersionUID = 304320309380200L;
	
	private final int throttleTiming;

	public ChatThrottleException(final int throttleTiming) {
		this.throttleTiming = throttleTiming;
	}

	public int getThrottleTiming() {
		return throttleTiming;
	}
}