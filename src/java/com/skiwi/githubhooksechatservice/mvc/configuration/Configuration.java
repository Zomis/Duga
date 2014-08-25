
package com.skiwi.githubhooksechatservice.mvc.configuration;

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
}
