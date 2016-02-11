package net.zomis.duga.chat;

import net.zomis.duga.chat.events.DugaEvent;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class TestBot implements ChatBot {

    Map<WebhookParameters, List<String>> messages = new HashMap<>();

    WebhookParameters debug = WebhookParameters.toRoom("debug");

    @Deprecated
    public void postDebug(String message) {
        this.postChat(debug, Collections.singletonList(message));
    }

    @Override
    public Future<List<ChatMessageResponse>> postChat(WebhookParameters params, List<String> messages) {
        this.messages.putIfAbsent(params, new ArrayList<>());
        this.messages.get(params).addAll(messages);
        return null;
    }

    @Override
    public void postSingle(WebhookParameters params, String message) {
        this.postChat(params, Collections.singletonList(message));
    }

    @Override
    public Future<ChatMessageResponse> postAsync(ChatMessage message) {
        return null;
    }

    @Override
    public ChatMessageResponse postNowOnce(ChatMessage message) {
        return null;
    }

    @Override
    public ChatMessageResponse postNow(ChatMessage message) {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public <E extends DugaEvent> void registerListener(Class<E> eventClass, Consumer<E> handler) {

    }

}
