package net.zomis.duga.tasks

import net.zomis.duga.DugaBot
import net.zomis.duga.User
import net.zomis.duga.chat.WebhookParameters
import org.apache.commons.lang.StringEscapeUtils

class ChatMessageIncoming {

    String content
    int event_type
    long message_id
    long room_id
    long time_stamp
    long user_id
    long parent_id
    long id
    String user_name
    int message_stars
    String room_name
    long target_user_id
    boolean show_parent

    DugaBot bot
    WebhookParameters params
    private User dugaUser

    User fetchUser() {
        if (dugaUser) {
            return dugaUser
        } else {
            User.withNewSession {status ->
                dugaUser = User.findByChatId(user_id)
            }
            return dugaUser
        }
    }

    void reply(String message) {
        bot.postSingle(params, ":$message_id $message")
    }

    void post(String message) {
        bot.postSingle(params, message)
    }

    void ping(String message) {
        bot.postSingle(params, "@$user_name $message")
    }

    def propertyMissing(String name, value) {
        println 'Missing Property: ' + name + ' = ' + value
    }

    @Override
    String toString() {
        return "Room $room_id: $user_name ($user_id) said $content"
    }

    void cleanHTML() {
        content = StringEscapeUtils.unescapeHtml(content)
    }
}
