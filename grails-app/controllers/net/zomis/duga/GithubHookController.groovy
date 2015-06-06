package net.zomis.duga

import com.gistlabs.mechanize.Resource
import com.gistlabs.mechanize.document.json.JsonDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class GithubHookController {

    static allowedMethods = [hook:'POST']

    @Autowired
    DugaBot bot

    @Autowired
    Environment environment

    def test() {
        render 'Hello World'
    }

    def chatTest() {
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

    def message() {
        def value = environment.getProperty('botName')
        bot.postChat('This is a test, ' + value)
        render 'Posted'
    }

    def hookText() {
        println 'JSON Data: ' + params
        println 'Request: ' + request
        println 'Request Text: ' + request.reader.text
        println 'Github Event: ' + request.getHeader('X-GitHub-Event')
        render 'OK'
    }

    def hookJson() {
        println 'JSON Data: ' + params
        println 'JSON Request: ' + request.JSON
        println 'Request: ' + request
        println 'Github Event: ' + request.getHeader('X-GitHub-Event')
        render 'OK'
    }

}
