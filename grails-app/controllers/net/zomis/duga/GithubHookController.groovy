package net.zomis.duga

import net.zomis.duga.chat.WebhookParameters
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired

class GithubHookController {

    static allowedMethods = [hook:'POST']

    @Autowired
    DugaBot bot

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
//        bot.postChat(WebhookParameters.toRoom(room), strings)
        render 'OK'
    }

}
