package net.zomis.duga.chat;

import java.util.*;

public class TestBot implements ChatBot {

    Map<WebhookParameters, List<String>> messages = new HashMap<>();

    WebhookParameters debug = WebhookParameters.toRoom("debug");

    @Override
    public void postDebug(String message) {
        this.postChat(debug, Collections.singletonList(message));
    }

    @Override
    public void postChat(WebhookParameters params, List<String> messages) {
        this.messages.putIfAbsent(params, new ArrayList<>());
        this.messages.get(params).addAll(messages);
    }

    @Override
    public void postSingle(WebhookParameters params, String message) {
        this.postChat(params, Collections.singletonList(message));
    }

}
