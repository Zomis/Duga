package net.zomis.duga.tasks.qscan

import net.zomis.duga.chat.ChatBot
import net.zomis.duga.GithubBean
import net.zomis.duga.HookStringification
import net.zomis.duga.StackAPI
import net.zomis.duga.chat.WebhookParameters

import java.time.Instant

class QuestionScanTask implements Runnable {

    private static final FILTER = "!DEQ-Ts0KBm6n14zYUs8UZUsw.yj0rZkhsEKF2rI4kBp*yOHv4z4"
    public static final String LATEST_QUESTIONS = 'questions?order=desc&sort=activity'

    private final StackAPI stackAPI
    private final GithubBean githubBean
    private final HookStringification hookString
    private final ChatBot bot
    private final String site
    private final String actions
    private final WebhookParameters params
    Instant lastCheck

    def QuestionScanTask(StackAPI stackExchangeAPI, GithubBean githubBean,
         HookStringification hookStringification, ChatBot dugaBot,
         String site, String actions, String room) {
        this.stackAPI = stackExchangeAPI
        this.githubBean = githubBean
        this.hookString = hookStringification
        this.bot = dugaBot
        this.site = site
        this.actions = actions
        this.params = WebhookParameters.toRoom(room)
        this.lastCheck = Instant.now()
    }

    @Override
    void run() {
        Instant previousCheck = this.lastCheck
        this.lastCheck = Instant.now()
        def questions = stackAPI.apiCall(LATEST_QUESTIONS, site, FILTER)

        if (actions.contains('answerInvalidation')) {
            AnswerInvalidationCheck.perform(questions, previousCheck, stackAPI, bot, params)
        }
    }

}
