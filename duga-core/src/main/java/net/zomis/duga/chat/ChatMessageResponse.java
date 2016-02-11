package net.zomis.duga.chat;

public class ChatMessageResponse {

    private final long id;
    private final long time;
    private final String fullResponse;
    private final Exception exception;

    public ChatMessageResponse(long id, long time, String fullResponse) {
        this.id = id;
        this.time = time;
        this.fullResponse = fullResponse;
        this.exception = null;
    }

    public ChatMessageResponse(String fullResponse, Exception exception) {
        this.id = 0;
        this.time = 0;
        this.fullResponse = fullResponse;
        this.exception = exception;
    }

    public long getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getFullResponse() {
        return fullResponse;
    }

    public Exception getException() {
        return exception;
    }

    public boolean hasException() {
        return exception != null;
    }

}
