package net.zomis.duga

import com.gistlabs.mechanize.Resource
import com.gistlabs.mechanize.document.json.JsonDocument
import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class BotController {

    static allowedMethods = [post:'POST']

    @Autowired
    DugaBot bot

    @Autowired
    Environment environment

    def post() {
        String text = request.reader.text
        println 'Request Text: ' + text
        bot.postChat(text)
        render 'OK'
    }

    def test() {
        def value = environment.getProperty('botName')
        bot.postChat('This is a test, ' + value)
        render 'Posted'
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
