package net.zomis.duga.chat.listen;

//import org.apache.commons.lang.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.zomis.duga.chat.ChatBot;
import net.zomis.duga.chat.WebhookParameters;

public class ChatMessageIncoming {

    @JsonProperty
    private String content;
    @JsonProperty("event_type")
    private int eventType;
    @JsonProperty("message_id")
    private long messageId;
    @JsonProperty("room_id")
    private long roomId;
    @JsonProperty("time_stamp")
    private long timestamp;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("parent_id")
    private long parentId;
    @JsonProperty("id")
    private long id;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("message_stars")
    private int messageStars;
    @JsonProperty("room_name")
    private String roomName;
    @JsonProperty("target_user_id")
    private long targetUserId;
    @JsonProperty("show_parent")
    private boolean showParent;

    ChatBot bot;
    WebhookParameters params;

    public void reply(String message) {
        bot.postAsync(params.message(":" + messageId + " " + message));
    }

    public void post(String message) {
        bot.postAsync(params.message(message));
    }

    public void ping(String message) {
        bot.postAsync(params.message("@$user_name $message"));
    }

    @Override
    public String toString() {
        return String.format("Room %s: %s (%d) said %s", roomId, userName, userId, content);
    }

    public int getEventType() {
        return eventType;
    }

    public int getMessageStars() {
        return messageStars;
    }

    public long getId() {
        return id;
    }

    public long getMessageId() {
        return messageId;
    }

    public long getParentId() {
        return parentId;
    }

    public long getRoomId() {
        return roomId;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getUserName() {
        return userName;
    }

//    public void cleanHTML() {
//        content = StringEscapeUtils.unescapeHtml(content);
//    }
}
