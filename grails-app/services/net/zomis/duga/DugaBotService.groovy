package net.zomis.duga

import net.zomis.duga.chat.BotConfiguration
import net.zomis.duga.chat.BotRoom
import net.zomis.duga.chat.ChatBot
import net.zomis.duga.chat.ChatMessage
import net.zomis.duga.chat.ChatMessageResponse
import net.zomis.duga.chat.StackExchangeChatBot
import net.zomis.duga.chat.events.DugaEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

import java.util.concurrent.Future
import java.util.function.Consumer

class DugaBotService implements ChatBot, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DugaBotService.class)

    @Autowired
    Environment environment

    private StackExchangeChatBot bot

    @Deprecated
    void postSingle(BotRoom params, String message) {
        this.postAsync(params.message(message))
    }

    @Override
    Future<List<ChatMessageResponse>> postChat(List<ChatMessage> messages) {
        messages.each {
            logger.info("postChat $it to $it.room")
        }
        return bot.postChat(messages)
    }

    @Override
    Future<ChatMessageResponse> postAsync(ChatMessage message) {
        bot.postAsync(message)
    }

    @Override
    ChatMessageResponse postNowOnce(ChatMessage message) {
        bot.postNowOnce(message)
    }

    @Override
    ChatMessageResponse postNow(ChatMessage message) {
        bot.postNow(message)
    }

    @Override
    void start() {
        bot.start()
    }

    @Override
    void stop() {
        bot.stop()
    }

    @Override
    BotRoom room(String s) {
        return bot.room(s)
    }

    @Override
    def <E extends DugaEvent> void registerListener(Class<E> eventClass, Consumer<E> handler) {
        bot.registerListener(eventClass, handler)
    }

    @Override
    void afterPropertiesSet() throws Exception {
        def config = new BotConfiguration()
        config.rootUrl = environment.getProperty('rootUrl')
        config.chatUrl = 'https://chat.stackexchange.com'
        config.botEmail = environment.getProperty('email')
        config.botPassword = environment.getProperty('password')
        config.chatThrottle = 10000
        config.chatMaxBurst = 2
        config.chatMinimumDelay = 500
        bot = new StackExchangeChatBot(config)
        bot.start()
    }

    String fkey() {
        bot.getFKey()
    }

}
