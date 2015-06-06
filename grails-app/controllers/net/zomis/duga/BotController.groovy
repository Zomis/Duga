package net.zomis.duga

import com.gistlabs.mechanize.Resource
import com.gistlabs.mechanize.document.json.JsonDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class BotController {

    static allowedMethods = [post:'POST']

    @Autowired
    DugaBot bot

    @Autowired
    Environment environment

    def post() {
        String text = request.reader.text
        println 'Request Text: ' + text
        bot.postChat(text)
        render 'OK'
    }

    def test() {
        def value = environment.getProperty('botName')
        bot.postChat('This is a test, ' + value)
        render 'Posted'
    }

    def latestMessages() {
        def agent = bot.agent()

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fkey", bot.fkey());
        parameters.put("mode", "messages");
        parameters.put("msgCount", String.valueOf(10));
        Resource response = agent.post("http://chat.stackexchange.com/chats/" + 16134 + "/events", parameters)
        println 'Response: ' + response.title
        println 'Response: ' + response
        if (response instanceof JsonDocument) {
            def json = response as JsonDocument
            println 'Root node: ' + json.root
        }
        render 'Something happened at least'
    }

}
