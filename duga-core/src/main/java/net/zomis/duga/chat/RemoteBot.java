package net.zomis.duga.chat;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RemoteBot {

    private static final String DEFAULT_DUGA_URL =
        "http://stats.zomis.net/GithubHookSEChatService/bot/jsonPost";

    private final String apiKey;
    private final String url;

    public RemoteBot(String apiKey) {
        this(DEFAULT_DUGA_URL, apiKey);
    }

    public RemoteBot(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
    }

    public String post(String roomId, String message) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String request = String.format("{\"roomId\": \"%s\", \"apiKey\": \"%s\"," +
                    "\"text\": \"%s\"}", roomId, apiKey, message);
            byte[] postData = request.getBytes(StandardCharsets.UTF_8);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);
            try (DataOutputStream it = new DataOutputStream(conn.getOutputStream())) {
                it.write(postData, 0, postData.length);
                it.flush();
            }
            InputStream is = conn.getInputStream();
            String result = IOUtils.toString(is, "UTF-8");
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//                "{ \"roomId\":\"16134\", \"apiKey\":\"" + apiKey +"\"," +
//                '"text": "' + fname + ']()" }'
    }

}
