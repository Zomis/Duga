package net.zomis.duga

import grails.transaction.Transactional
import net.zomis.duga.chat.WebhookParameters
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class BotController {

    static allowedMethods = [post:'POST']

    @Autowired
    DugaBotService bot

    @Autowired
    Environment environment

    @Transactional(readOnly = true)
    def jsonPost() {
        println 'json post'
        JSONObject json = request.JSON
        println json
        println request
        println params

        String text = json.text
        header 'Access-Control-Allow-Origin', '*'
        if (!text) {
            render 'No text found'
            return
        }
        String roomId = json.roomId
        WebhookParameters roomParams = WebhookParameters.toRoom(roomId)

        User user = User.findByApiKey(json.apiKey)
        if (user) {
            for (Authority auth : user.authorities) {
                if (auth.authority == 'ROLE_ADMIN') {
                    println 'Request Text: ' + text
                    bot.postSingle(roomParams, text)
                    def result = render 'OK'
                    return result
                }
            }
            render 'Unauthorized'
        } else {
            render 'User not found'
        }
    }

    @Transactional(readOnly = true)
    def post() {
        println 'post'
        Map parameters = request.getParameterMap();
        println parameters
        header 'Access-Control-Allow-Origin', '*'
        if (parameters.size() != 3) {
            render 'Expected three parameters: Room, apiKey, and text'
            return
        }
        def pars = new ArrayList<>(parameters.entrySet())
        String text = pars[2].key
        if (!text) {
            render 'No text found'
            return
        }
        String roomId = params.roomId
        WebhookParameters roomParams = WebhookParameters.toRoom(roomId)

        User user = User.findByApiKey(params.apiKey)
        if (user) {
            for (Authority auth : user.authorities) {
                if (auth.authority == 'ROLE_ADMIN') {
                    println 'Request Text: ' + text
                    bot.postSingle(roomParams, text)
                    def result = render 'OK'
                    return result
                }
            }
            render 'Unauthorized'
        } else {
            render 'User not found'
        }
    }

    @Transactional
    def addTask() {
        def task = new TaskData()
        task.taskValue = 'no task defined'
        task.cronStr = '0 0 * * * *'
        task.save(failOnError: true, flush: true)
        render 'Posted: ' + task
    }

    @Transactional
    def addOther() {
        def daily = new DailyInfo()
        daily.additions = 4
        daily.comment = 'test'
        daily.commits = 1
        daily.deletions = 0
        daily.issuesClosed = 2
        daily.issuesOpened = 42
        daily.name = 'Test'
        daily.url = 'http://github.com/Zomis/Duga'
        def saved = daily.save(flush: true)
        render 'Saved: ' + daily + saved
    }

}
