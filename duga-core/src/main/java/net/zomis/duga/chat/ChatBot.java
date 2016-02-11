package net.zomis.duga.chat;

import net.zomis.duga.chat.events.DugaEvent;
import net.zomis.duga.chat.events.DugaStartedEvent;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface ChatBot {

    Future<List<ChatMessageResponse>> postChat(WebhookParameters params, List<String> messages);

    void postSingle(WebhookParameters params, String message);

    Future<ChatMessageResponse> postAsync(ChatMessage message);

    /**
     * Try to post a message once and return result no matter what the response is
     * @return Response with details of success or error
     */
    ChatMessageResponse postNowOnce(ChatMessage message);

    /**
     * Repeatedly try to post a message until either success or a serious error occurs.
     * @return Response with details of success or error
     */
    ChatMessageResponse postNow(ChatMessage message);

    void start();

    void stop();

    /**
     * Add an event listener. Note that all events are run synchronously.
     *
     * @param eventClass Event class
     * @param handler Handler for the event class
     * @param <E> Event class
     */
    <E extends DugaEvent> void registerListener(Class<E> eventClass, Consumer<E> handler);
}
