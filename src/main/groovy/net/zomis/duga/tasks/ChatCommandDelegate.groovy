package net.zomis.duga.tasks

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import net.zomis.duga.DugaChatListener
import net.zomis.duga.User
import net.zomis.duga.chat.WebhookParameters

import java.util.concurrent.Callable

/**
 * Delegate for running chat commands
 */
abstract class ChatCommandDelegate extends Script {

    private ChatMessageIncoming message
    private DugaChatListener bean

    ChatCommandDelegate() {
        // this constructor intentionally left blank
    }

    void init(ChatMessageIncoming message, DugaChatListener bean) {
        if (this.message || this.bean) {
            throw new IllegalStateException('message and bean can only be initialized once')
        }
        this.message = message
        this.bean = bean
    }

    Closure ping = {
        message.reply('pong!')
    }

    Map say(String text) {
        [inRoom: {int id ->
            bean.chatBot.postSingle(WebhookParameters.toRoom(Integer.toString(id)), text)
        }, default: {
            message.reply(text)
        }]
    }

    ChatMessageIncoming getMessage() {
        message
    }

    DugaChatListener getBean() {
        bean
    }

    def register(String githubKey) {
        User.withNewSession {status ->
            User user = User.findByPingExpect(githubKey)
            if (user == null) {
                message.reply('No such user found.')
            } else {
                user.setAccountLocked(false)
                user.setChatName(message.user_name)
                user.chatId = message.user_id
                if (!user.save(failOnError: true, flush: true)) {
                    message.reply('Unable to save')
                    user.errors.each {
                        message.reply it
                    }
                } else {
                    message.reply('You have been registered!')
                }
            }
        }
    }

}
