package net.zomis.duga.chat.listen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gistlabs.mechanize.Resource;
import com.gistlabs.mechanize.document.json.JsonDocument;
import com.gistlabs.mechanize.document.json.node.JsonNode;
import com.gistlabs.mechanize.impl.MechanizeAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class StackExchangeFetch implements ChatMessageRetriever {

    private static final Logger logger = LoggerFactory.getLogger(StackExchangeFetch.class);

    private final MechanizeAgent agent;
    private final Supplier<String> fkey;

    public StackExchangeFetch(Supplier<String> fkey) {
        this.agent = new MechanizeAgent();
        this.fkey = fkey;
    }

    @Override
    public List<ChatMessageIncoming> fetch(String roomId, int count) {
        Map<String, String> parameters = new HashMap<>();
        if (fkey == null) {
            return null;
        }
        parameters.put("fkey", fkey.get());
        parameters.put("mode", "messages");
        parameters.put("msgCount", String.valueOf(count));
        Resource response;
        try {
            response = agent.post("http://chat.stackexchange.com/chats/" +
                roomId + "/events", parameters);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        if (!(response instanceof JsonDocument)) {
            logger.warn("Unexpected response fetching " + roomId + ": " + response);
            return null;
        }

        logger.debug("Checking for events in room " + roomId);
        JsonDocument jsonDocument = (JsonDocument) response;
        JsonNode node = jsonDocument.getRoot();

        ObjectMapper mapper = new ObjectMapper();
        try {
            ListenRoot root = mapper.readValue(node.toString(), ListenRoot.class);
            return root.getEvents();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
