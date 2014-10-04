
package com.skiwi.githubhooksechatservice.mvc.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Frank van Heeswijk
 */
public class Configuration {
    private String rootUrl;
    private String chatUrl;
    
    private String botEmail;
    private String botPassword;
    
    private String roomId;
    
    private int chatThrottle;
    private int chatMaxBurst;
    private int chatMinimumDelay;
	
	private boolean deployGreetingEnabled;
	private String deployGreetingText;
	
	private boolean undeployGoodbyeEnabled;
	private String undeployGoodbyeText;
	
	private String userMappings;
	private Map<String, String> userMappingsMap = new HashMap<>();

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(final String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(final String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public String getBotEmail() {
        return botEmail;
    }

    public void setBotEmail(final String botEmail) {
        this.botEmail = botEmail;
    }

    public String getBotPassword() {
        return botPassword;
    }

    public void setBotPassword(final String botPassword) {
        this.botPassword = botPassword;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }

    public int getChatThrottle() {
        return chatThrottle;
    }

    public void setChatThrottle(final int chatThrottle) {
        this.chatThrottle = chatThrottle;
    }

    public int getChatMaxBurst() {
        return chatMaxBurst;
    }

    public void setChatMaxBurst(final int chatMaxBurst) {
        this.chatMaxBurst = chatMaxBurst;
    }

    public int getChatMinimumDelay() {
        return chatMinimumDelay;
    }

    public void setChatMinimumDelay(final int chatMinimumDelay) {
        this.chatMinimumDelay = chatMinimumDelay;
    }

	public boolean getDeployGreetingOn() {
		return deployGreetingEnabled;
	}

	public void setDeployGreetingEnabled(final boolean deployGreetingEnabled) {
		this.deployGreetingEnabled = deployGreetingEnabled;
	}

	public String getDeployGreetingText() {
		return deployGreetingText;
	}

	public void setDeployGreetingText(final String deployGreetingText) {
		this.deployGreetingText = deployGreetingText;
	}

	public boolean getUndeployGoodbyeEnabled() {
		return undeployGoodbyeEnabled;
	}

	public void setUndeployGoodbyeEnabled(final boolean undeployGoodbyeEnabled) {
		this.undeployGoodbyeEnabled = undeployGoodbyeEnabled;
	}

	public String getUndeployGoodbyeText() {
		return undeployGoodbyeText;
	}

	public void setUndeployGoodbyeText(final String undeployGoodbyeText) {
		this.undeployGoodbyeText = undeployGoodbyeText;
	}

	public String getUserMappings() {
		return userMappings;
	}

	public void setUserMappings(final String userMappings) {
		this.userMappings = userMappings;
		this.userMappingsMap = Arrays.stream(userMappings.split(","))
			.filter(mapping -> mapping.contains("->"))
			.map(mapping -> Arrays.asList(mapping.split("->")))
			.collect(Collectors.toMap(list -> list.get(0).trim(), list -> list.get(1).trim()));
	}

	public Map<String, String> getUserMappingsMap() {
		return userMappingsMap;
	}
}
