package net.zomis.duga

import net.zomis.duga.chat.BotConfiguration
import net.zomis.duga.chat.StackExchangeChatBot
import net.zomis.duga.chat.WebhookParameters
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class DugaBot implements InitializingBean {

    @Autowired
    Environment environment

    private StackExchangeChatBot bot

    def postChat(String message) {
        bot.postMessages(WebhookParameters.toRoom('16134'), ['Hello World! ' + message])
    }

    @Override
    void afterPropertiesSet() throws Exception {
        bot = new StackExchangeChatBot(new BotConfiguration().init(environment))
    }
}
