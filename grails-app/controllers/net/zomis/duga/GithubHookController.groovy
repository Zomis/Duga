package net.zomis.duga

import org.springframework.beans.factory.annotation.Autowired

class GithubHookController {

    static allowedMethods = [hook:'POST']

    @Autowired
    DugaBot bot

    def hook() {
        println 'JSON Data: ' + params
        println 'JSON Request: ' + request.JSON
        println 'Request: ' + request
        println 'Room: ' + params?.roomId
        println 'Github Event: ' + request.getHeader('X-GitHub-Event')
        render 'OK'
    }

}
