package net.zomis.duga

import net.zomis.duga.chat.WebhookParameters

interface ChatBot {

    void postChat(WebhookParameters params, List<String> messages);

}