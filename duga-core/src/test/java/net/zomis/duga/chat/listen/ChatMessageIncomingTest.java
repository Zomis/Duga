package net.zomis.duga.chat.listen;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ChatMessageIncomingTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void cleanHTML() throws IOException {
        ChatMessageIncoming msg = fromString("{ \"content\": \"this is a &quot;quote&quot;\" }");
        assertEquals("this is a \"quote\"", msg.cleanHTML());
    }

    private ChatMessageIncoming fromString(String json) throws IOException {
        return mapper.readValue(json, ChatMessageIncoming.class);
    }

}
