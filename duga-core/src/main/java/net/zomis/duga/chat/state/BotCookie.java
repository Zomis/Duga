package net.zomis.duga.chat.state;

public class BotCookie {

    private String name;
    private String value;
    private String domain;

    public static BotCookie create(String name, String value, String domain) {
        BotCookie cookie = new BotCookie();
        cookie.name = name;
        cookie.value = value;
        cookie.domain = domain;
        return cookie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
