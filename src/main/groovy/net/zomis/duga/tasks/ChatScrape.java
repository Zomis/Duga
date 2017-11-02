package net.zomis.duga.tasks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatScrape {

    private static final Logger logger = LoggerFactory.getLogger(ChatScrape.class);

    public String fetch(long messageId) throws IOException {
        String url = "http://chat.stackexchange.com/transcript/message/" +
            messageId + "#" + messageId;
        logger.info("Fetching URL " + url);
        Document doc = Jsoup.connect(url).get();
        doc.select(".message:not(.highlight)").remove();
        List<String> texts = texts(doc);
        texts.forEach(logger::info);
        return texts.get(0);
    }

    private static List<String> texts(Document doc) {
        Elements results = doc.select(".message .content");
        if (results.select(".quote").size() > 0) {
            results = results.select(".quote");
        }

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
