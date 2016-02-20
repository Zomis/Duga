package net.zomis.duga

import net.zomis.duga.chat.BotRoom
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired

class GithubHookController {

    static allowedMethods = [hook:'POST']

    @Autowired
    DugaBotService bot

    @Autowired
    HookStringification stringification

    def hook() {
        String eventType = request.getHeader('X-GitHub-Event')
        String room = params?.roomId
        JSONObject json = request.JSON
        println 'JSON Data: ' + params
        println 'JSON Request: ' + json
        println 'Request: ' + request
        println 'Room: ' + room
        println 'Github Event: ' + eventType

        List<String> strings = stringification.postGithub(eventType, json)
        strings.forEach({ println it })
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
