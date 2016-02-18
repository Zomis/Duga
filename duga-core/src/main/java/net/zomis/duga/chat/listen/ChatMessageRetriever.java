package net.zomis.duga.chat.listen;

import java.util.List;

public interface ChatMessageRetriever {

    List<ChatMessageIncoming> fetch(String roomId, int count);

}
