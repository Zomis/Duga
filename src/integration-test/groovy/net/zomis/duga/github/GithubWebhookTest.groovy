package net.zomis.duga.github

import groovy.json.JsonSlurper
import net.zomis.duga.DugaStats
import net.zomis.duga.HookStringification
import net.zomis.duga.chat.WebhookParameters
import net.zomis.duga.tasks.qscan.TestBot
import org.junit.Before
import org.junit.Test;

class GithubWebhookTest {

    private HookStringification stringer

    @Before
    void setup() {
        stringer = new HookStringification();
        stringer.stats = new DugaStats() {
            @Override void addCommit(def repo, def commit) {}
            @Override void addIssueComment(def Object repo) {}
            @Override def addIssue(def Object repo, int delta) {}
        }
    }

    @Test
    void pullRequestOpen() {
        def obj = new JsonSlurper().parseText(getClass().classLoader.getResourceAsStream('payload-pr-opened.json').text)
        def result = stringer.postGithub('pull_request', obj)
        def bot = new TestBot()
        def param = WebhookParameters.toRoom('hookTest')
        bot.postChat(param, result)

        assert bot.messages[param] ==
               ['**\\[[Cardshifter/HTML-Client](https://github.com/Cardshifter/HTML-Client)\\]** ' +
                '[**SirPython**](https://github.com/SirPython) created pull request ' +
                '[**#98: Develop**](https://github.com/Cardshifter/HTML-Client/pull/98) to merge ' +
                '[**develop**](https://github.com/Cardshifter/HTML-Client/tree/develop) into ' +
                '[**deploy-script**](https://github.com/Cardshifter/HTML-Client/tree/deploy-script)',
           '> updating deploy-script']
    }

}
