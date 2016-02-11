package net.zomis.duga.chat.events;

import net.zomis.duga.chat.ChatBot;

public class DugaEvent {

    private final ChatBot bot;

    public DugaEvent(ChatBot bot) {
        this.bot = bot;
    }

    public ChatBot getBot() {
        return bot;
    }

}
