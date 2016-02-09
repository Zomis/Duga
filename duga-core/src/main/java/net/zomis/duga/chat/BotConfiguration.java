package net.zomis.duga.chat

import org.springframework.core.env.Environment;

import java.util.stream.Collectors;

/**
 *
 * @author Frank van Heeswijk
 */
class BotConfiguration {
    private String rootUrl;
    private String chatUrl;

    private String botEmail;
    private String botPassword;

    private String roomId;

    private String stackAPIKey;

    private int chatThrottle;
    private int chatMaxBurst;
    private int chatMinimumDelay;

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

    public String getUserMappings() {
        return userMappings;
    }

    public void setUserMappings(final String userMappings) {
        this.userMappings = userMappings;
        this.userMappingsMap = Arrays.stream(userMappings.split(","))
                .filter({mapping -> mapping.contains("->")})
                .map({mapping -> Arrays.asList(mapping.split("->"))})
                .collect(Collectors.toMap({list -> list.get(0).trim()}, {list -> list.get(1).trim()}));
    }

    public Map<String, String> getUserMappingsMap() {
        return userMappingsMap;
    }

    public String getStackAPIKey() {
        return stackAPIKey;
    }

    public void setStackAPIKey(String stackAPIKey) {
        this.stackAPIKey = stackAPIKey;
    }

    def init(Environment env) {
        rootUrl = env.getProperty('rootUrl')
        chatUrl = 'http://chat.stackexchange.com'
        botEmail = env.getProperty('email')
        botPassword = env.getProperty('password')
        roomId = 16134
        chatThrottle = 10000
        chatMaxBurst = 2
        chatMinimumDelay = 500
        userMappings = [skiwi2: 'skiwi', Zomis: 'SimonAndréForsberg', janosgyerik: 'janos']
        stackAPIKey = env.getProperty('stackAPI')
        return this
    }

}