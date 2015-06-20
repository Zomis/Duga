package net.zomis.duga.tasks

import net.zomis.duga.DugaChatListener
import net.zomis.duga.User
import net.zomis.duga.chat.WebhookParameters

/**
 * Delegate for running chat commands
 */
class ChatCommandDelegate {

    private final ChatMessageIncoming message
    private final DugaChatListener bean

    ChatCommandDelegate(ChatMessageIncoming chatMessageIncoming, DugaChatListener bean) {
        this.message = chatMessageIncoming
        this.bean = bean
    }

    Closure ping = {
        message.reply('pong!')
    }

    def say(String text) {
        [inRoom: {int id ->
            bean.chatBot.postSingle(WebhookParameters.toRoom(Integer.toString(id)), text)
        }, default: {
            message.reply(text)
        }]
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
