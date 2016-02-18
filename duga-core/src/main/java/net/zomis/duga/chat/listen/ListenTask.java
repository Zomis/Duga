package net.zomis.duga.chat.listen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gistlabs.mechanize.Resource;
import com.gistlabs.mechanize.document.json.JsonDocument;
import com.gistlabs.mechanize.document.json.node.JsonNode;
import com.gistlabs.mechanize.impl.MechanizeAgent;
import net.zomis.duga.chat.BotRoom;
import net.zomis.duga.chat.ChatBot;
import net.zomis.duga.chat.StackExchangeChatBot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ListenTask implements Runnable {

    private static final int NUM_MESSAGES = 10;

    private final ChatBot bot;
    private final String room;
    private final BotRoom params;
    private final ChatMessageRetriever retriever;
    private long lastHandledId;
    private long lastMessageTime;
    private final Consumer<ChatMessageIncoming> handler;

    public ListenTask(ChatBot bot, ChatMessageRetriever retriever,
          String room, Consumer<ChatMessageIncoming> handler) {
        this.bot = bot;
        this.room = room;
        this.params = BotRoom.toRoom(room);
        this.retriever = retriever;
        this.handler = handler;
    }

    synchronized void latestMessages() {
        List<ChatMessageIncoming> events = retriever.fetch(room, NUM_MESSAGES);
        long previousId = lastHandledId;
        for (ChatMessageIncoming message : events) {
            message.bot = bot;
            message.params = params;
            if (message.getMessageId() <= lastHandledId) {
                continue;
            }
            lastHandledId = Math.max(lastHandledId, message.getMessageId());
            lastMessageTime = Math.max(lastMessageTime, message.getTimestamp());
            if (previousId <= 0) {
                System.out.println("Previous id 0, skipping " + message.getContent());
                continue;
            }

            handler.accept(message);
        }
        if (previousId <= 0) {
            bot.postAsync(params.message("Monking! (Duga is now listening for commands)"));
        }
    }

    @Override
    public void run() {
        try {
            latestMessages();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }
}
