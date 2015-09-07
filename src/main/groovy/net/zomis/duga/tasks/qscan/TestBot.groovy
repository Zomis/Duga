package net.zomis.duga.tasks.qscan

import net.zomis.duga.ChatBot
import net.zomis.duga.chat.WebhookParameters

class TestBot implements ChatBot {

    Map<WebhookParameters, List<String>> messages = [:]

    @Override
    void postChat(WebhookParameters params, List<String> messages) {
        this.messages.putIfAbsent(params, new ArrayList<String>())
        this.messages.get(params).addAll(messages)
    }

}
