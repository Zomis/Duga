package net.zomis.duga.tasks

import net.zomis.duga.ChatCommands

/**
 * Delegate for running chat commands
 */
class ChatCommandDelegate {

    private final ChatMessageIncoming message
    private final ChatCommands handler

    ChatCommandDelegate(ChatMessageIncoming chatMessageIncoming, ChatCommands chatCommands) {
        this.message = chatMessageIncoming
        this.handler = chatCommands
    }

    Closure ping = {
        message.reply('debug pong')
    }

    void say(String text) {
        message.reply(text)
    }

}
