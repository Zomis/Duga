package net.zomis.duga.chat;

import java.util.List;
import java.util.concurrent.Future;

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
}
