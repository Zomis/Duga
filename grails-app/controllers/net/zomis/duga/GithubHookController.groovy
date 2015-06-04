package net.zomis.duga

import org.grails.web.json.JSONObject

class GithubHookController {

    static allowedMethods = [hook:'POST']

    def test() {
        render 'Hello World'
    }

    def hook() {
        println 'JSON Data: ' + params
        println 'JSON Request: ' + request.JSON
        println 'Request: ' + request
        println 'Github Event: ' + request.getHeader('X-GitHub-Event')
        render 'OK'
    }

}
