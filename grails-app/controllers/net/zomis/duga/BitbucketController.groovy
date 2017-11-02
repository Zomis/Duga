package net.zomis.duga

import net.zomis.duga.chat.BotRoom
import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class BitbucketController {

    private static final Logger logger = LoggerFactory.getLogger(BitbucketController.class);

    static allowedMethods = [bitbucket:'POST']

    @Autowired
    DugaBotService bot

    @Autowired
    BitbucketStringification stringificationBitbucket

    def bitbucket() {
        String eventType = request.getHeader('X-Event-Key')
        String room = params?.roomId
        JSONObject json = request.JSON
        logger.info('JSON Data: ' + params)
        logger.info('JSON Request: ' + json)
        logger.info('Request: ' + request)
        logger.info('Room: ' + room)
        logger.info('Github Event: ' + eventType)

        List<String> strings = stringificationBitbucket.postBitbucket(eventType, json)
        strings.forEach({ logger.info(it) })
        if (room == null) {
            room = '16134'
        }
        String[] rooms = room.split(',')
        for (String postRoom : rooms) {
            BotRoom hookParams = bot.room(postRoom)
            bot.postChat(hookParams.messages(strings))
        }
        render 'OK'
    }

}
