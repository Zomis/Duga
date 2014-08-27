
package com.skiwi.githubhooksechatservice.mvc.beans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.mvc.configuration.Configuration;

/**
 *
 * @author Frank van Heeswijk
 */
public class StartupBean implements DisposableBean {
    @Autowired
    private Configuration configuration;
    
    @Autowired
    private ChatBot chatBot;

	private Thread thread;
    
    public void start() {
        thread = new Thread(chatBot::start);
        thread.start();
    }

	@Override
	public void destroy() throws Exception {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}
	}
}
