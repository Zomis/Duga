package net.zomis.duga.chat.listen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gistlabs.mechanize.Resource;
import com.gistlabs.mechanize.document.json.JsonDocument;
import com.gistlabs.mechanize.document.json.node.JsonNode;
import com.gistlabs.mechanize.impl.MechanizeAgent;
import net.zomis.duga.chat.StackExchangeChatBot;
import net.zomis.duga.chat.WebhookParameters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ListenTask implements Runnable {

    private static final int NUM_MESSAGES = 10;

    private final StackExchangeChatBot bot;
    private final String room;
    private final WebhookParameters params;
    private long lastHandledId;
    private long lastMessageTime;
    private MechanizeAgent agent;
    private final Consumer<ChatMessageIncoming> handler;

    public ListenTask(StackExchangeChatBot bot, String room, Consumer<ChatMessageIncoming> handler) {
        this.bot = bot;
        this.room = room;
        this.params = WebhookParameters.toRoom(room);
        this.agent = new MechanizeAgent();
        this.handler = handler;
    }

    synchronized void latestMessages() {
        Map<String, String> parameters = new HashMap<>();
        String fkey = bot.getFKey();
        if (fkey == null) {
            return;
        }
        parameters.put("fkey", fkey);
        parameters.put("mode", "messages");
        parameters.put("msgCount", String.valueOf(NUM_MESSAGES));
        Resource response;
        try {
            response = agent.post("http://chat.stackexchange.com/chats/" + room + "/events", parameters);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        if (!(response instanceof JsonDocument)) {
            System.out.println("Unexpected response: " + response);
            return;
        }

        System.out.println("Checking for events in room " + room);
        JsonDocument jsonDocument = (JsonDocument) response;
        JsonNode node = jsonDocument.getRoot();

        ObjectMapper mapper = new ObjectMapper();
        ListenRoot root;
        try {
            root = mapper.readValue(node.toString(), ListenRoot.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ChatMessageIncoming> events = root.getEvents();
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
            bot.postSingle(params, "Monking! (Duga is now listening for commands)");
        }
/*            Root node: {"ms":4,"time":41194973,"sync":1433551091,"events":
                [{"room_id":16134,"event_type":1,"time_stamp":1433547911,"user_id":125580,"user_name":"Duga","message_id":22039309,"content":"Loki Astari vs. Simon Andr&#233; Forsberg: 4383 diff. Year: -1368. Quarter: -69. Month: -5. Week: +60. Day: -25."}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433548817,"user_id":125580,"user_name":"Duga","message_id":22039366,"content":"<b><i>RELOAD!<\/i><\/b>"}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433548849,"user_id":125580,"user_name":"Duga","message_id":22039371,"content":"&#91;<a href=\"https://github.com/retailcoder/Rubberduck\" rel=\"nofollow\"><b>retailcoder/Rubberduck<\/b><\/a>&#93; 12 commits. 2 closed issues. 5 issue comments."}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433551821,"user_id":98071,"user_name":"Simon André Forsberg","parent_id":22039371,"show_parent":true,"message_id":22039768,"content":"@Duga No open issues today?"}]}

        Success: {"id":22039802,"time":1433552063}
*/
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
