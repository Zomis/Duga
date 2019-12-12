package net.zomis.duga.chat.events;

import net.zomis.duga.chat.ChatBot;

/**
 * Executed after bot has logged in, whether successful or not.
 */
public class DugaLoginEvent extends DugaEvent {

    public DugaLoginEvent(ChatBot bot) {
        super(bot);
    }

}
