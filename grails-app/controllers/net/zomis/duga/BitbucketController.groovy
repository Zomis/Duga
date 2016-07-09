package net.zomis.duga

import net.zomis.duga.chat.BotRoom
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired

class BitbucketController {

    static allowedMethods = [bitbucket:'POST']

    @Autowired
    DugaBotService bot

    @Autowired
    BitbucketStringification stringificationBitbucket

    def bitbucket() {
        String eventType = request.getHeader('X-Event-Key')
        String room = params?.roomId
        JSONObject json = request.JSON
        println 'JSON Data: ' + params
        println 'JSON Request: ' + json
        println 'Request: ' + request
        println 'Room: ' + room
        println 'Github Event: ' + eventType

        List<String> strings = stringificationBitbucket.postBitbucket(eventType, json)
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
