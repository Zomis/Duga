package net.zomis.duga.tasks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatScrape {

    public String fetch(long messageId) throws IOException {
        String url = "http://chat.stackexchange.com/transcript/message/" +
            messageId + "#" + messageId;
        System.out.println("Fetching URL " + url);
        Document doc = Jsoup.connect(url).get();
        doc.select(".message:not(.highlight)").remove();
        List<String> texts = texts(doc);
        texts.forEach(System.out::println);
        return texts.get(0);
    }

    private static List<String> texts(Document doc) {
        Elements results = doc.select(".message .content .quote");

        // Remove time stamp and comment link
        results.select("span.relativetime").parents().remove();

        List<String> result = new ArrayList<>(results.size());
        // Remove user name and link
        for (Element el : results) {
            Element rem = el.select("a").last();
            if (rem != null) {
                rem.remove();
            }
            result.add(el.html().replace("\n", ""));
        }
        return result;
    }

}
