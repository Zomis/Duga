package net.zomis.duga.chat;

public class BotConfiguration {
    private String rootUrl;
    private String chatUrl;

    private String botEmail;
    private String botPassword;

    private int chatThrottle;
    private int chatMaxBurst;
    private int chatMinimumDelay;

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

}