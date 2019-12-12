package net.zomis.duga.chat.state;

import java.util.List;

public class BotState {

    private String fkey;
    private List<BotCookie> cookies;

    public void setCookies(List<BotCookie> cookies) {
        this.cookies = cookies;
    }

    public void setFkey(String fkey) {
        this.fkey = fkey;
    }

    public List<BotCookie> getCookies() {
        return cookies;
    }

    public String getFkey() {
        return fkey;
    }
}
