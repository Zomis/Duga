package net.zomis.duga

import net.zomis.duga.chat.BotRoom
import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class GithubHookController {

    private static final Logger logger = LoggerFactory.getLogger(GithubHookController.class)

    static allowedMethods = [hook:'POST']

    @Autowired
    DugaBotService bot

    @Autowired
    HookStringification stringification

    def hook() {
        String eventType = request.getHeader('X-GitHub-Event')
        String room = params?.roomId
        JSONObject json = request.JSON

        logger.info('Received hook: {} to room {}: {}', eventType, room, json)

        List<String> strings = stringification.postGithub(eventType, json)
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
