package net.zomis.duga.chat;

import net.zomis.duga.chat.events.DugaEvent;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TestBot implements ChatBot {

    private Map<BotRoom, List<String>> messages = new HashMap<>();

    @Override
    public Future<List<ChatMessageResponse>> postChat(List<ChatMessage> messages) {
        BotRoom params = BotRoom.toRoom(messages.get(0).getRoom());
        this.messages.putIfAbsent(params, new ArrayList<>());
        this.messages.get(params).addAll(messages.stream()
            .map(ChatMessage::getMessage)
            .collect(Collectors.toList()));
        return null;
    }

    @Override
    public Future<ChatMessageResponse> postAsync(ChatMessage message) {
        postChat(Arrays.asList(message));
        return null;
    }

    @Override
    public ChatMessageResponse postNowOnce(ChatMessage message) {
        postChat(Arrays.asList(message));
        return null;
    }

    @Override
    public ChatMessageResponse postNow(ChatMessage message) {
        postChat(Arrays.asList(message));
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public BotRoom room(String roomId) {
        return new BotRoom(this, roomId);
    }

    @Override
    public <E extends DugaEvent> void registerListener(Class<E> eventClass, Consumer<E> handler) {

    }

    public Map<BotRoom, List<String>> getMessages() {
        return messages;
    }

}
