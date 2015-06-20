package net.zomis.duga.tasks

import net.zomis.duga.DugaChatListener
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
        message.reply('debug pong')
    }

    def say(String text) {
        [inRoom: {int id ->
            bean.chatBot.postSingle(WebhookParameters.toRoom(Integer.toString(id)), text)
        }, default: {
            message.reply(text)
        }]
    }

}
