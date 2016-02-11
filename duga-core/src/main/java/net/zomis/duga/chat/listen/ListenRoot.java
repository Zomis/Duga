package net.zomis.duga.chat.listen;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ListenRoot {

    @JsonProperty
    private int ms;
    @JsonProperty
    private long time;
    @JsonProperty
    private long sync;

    @JsonProperty
    private List<ChatMessageIncoming> events;

    public int getMs() {
        return ms;
    }

    public List<ChatMessageIncoming> getEvents() {
        return events;
    }

    public long getSync() {
        return sync;
    }

    public long getTime() {
        return time;
    }

}
