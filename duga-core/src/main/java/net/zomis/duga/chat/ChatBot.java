package net.zomis.duga.chat;

import java.util.List;

public interface ChatBot {

    void postDebug(String message);
    void postChat(WebhookParameters params, List<String> messages);

}
