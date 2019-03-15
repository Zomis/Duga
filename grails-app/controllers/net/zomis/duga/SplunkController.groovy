package net.zomis.duga

import net.zomis.duga.chat.BotRoom
import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class SplunkController {

    private static final Logger logger = LoggerFactory.getLogger(ManualStatsController.class)

    static allowedMethods = [webhook:'POST']

    @Autowired
    DugaBotService bot

    def webhook() {
        JSONObject json = request.JSON
        logger.info("Splunk Webhook Triggered: $json")

        String room = params?.roomId ?: "16134"
        BotRoom hookParams = bot.room(room)
        List<String> strings = stringify(json)
        bot.postChat(hookParams.messages(strings))
        render('OK')
    }

    static List<String> stringify(JSONObject json) {
        return ["**Splunk Alert:** ${json.search_name} - ${json.result}".toString()]
    }

}
