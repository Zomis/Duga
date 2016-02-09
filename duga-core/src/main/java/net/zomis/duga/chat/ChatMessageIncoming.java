package net.zomis.duga.chat;

//import org.apache.commons.lang.StringEscapeUtils;

class ChatMessageIncoming {

    String content;
    int event_type;
    long message_id;
    long room_id;
    long time_stamp;
    long user_id;
    long parent_id;
    long id;
    String user_name;
    int message_stars;
    String room_name;
    long target_user_id;
    boolean show_parent;

    ChatBot bot;
    WebhookParameters params;

    void reply(String message) {
        bot.postSingle(params, ":" + message_id + " " + message);
    }

    void post(String message) {
        bot.postSingle(params, message);
    }

    void ping(String message) {
        bot.postSingle(params, "@$user_name $message");
    }

    @Override
    public String toString() {
        return "Room $room_id: $user_name ($user_id) said $content";
    }

//    public void cleanHTML() {
//        content = StringEscapeUtils.unescapeHtml(content);
//    }
}
