package net.zomis.duga.github

import groovy.json.JsonSlurper
import net.zomis.duga.BitbucketStringification
import net.zomis.duga.DugaStats
import net.zomis.duga.chat.TestBot
import org.junit.Before
import org.junit.Test

class BitbucketTest {

    private final BitbucketStringification hook = new BitbucketStringification()

    @Before
    public void setup() {
        hook.stats = new DugaStats() {
            @Override void addCommit(def repo, def commit) {}
            @Override void addCommitBitbucket(Object repo, Object commit) {}
            @Override void addIssueComment(def Object repo) {}
            @Override def addIssue(def Object repo, int delta) {}
        }
    }

    @Test
    void pushCommit() {
        /*
        * repository.links.html.href
        * actor.username
        * actor.links.html.href
        * repository.full_name
        * push.changes[].old.name
        * push.changes[].commits[]
        * - hash
        * - links.html.href
        * - message
        * */

        String type = 'repo:push'
        String file = 'bitbucket/push-commit1.json'
        List<String> messages =  ['**\\[[SimonForsberg/minesweeper-ai](https://bitbucket.org/SimonForsberg/minesweeper-ai)]** ' +
          '[**Simon Forsberg**](https://bitbucket.org/SimonForsberg/) pushed commit ' +
          '[**ac97f33**](https://bitbucket.org/SimonForsberg/minesweeper-ai/commits/ac97f33fbee6b3d2ddf27a38394db9f134c94b12) to ' +
          '[**master**](https://bitbucket.org/SimonForsberg/minesweeper-ai/branch/master): Fix AI_Challenger for new API'] as List<String>
        def stream = getClass().classLoader.getResourceAsStream(file)
        assert stream : "No stream found for '$file'"
        def obj = new JsonSlurper().parseText(stream.text)
        def result = hook.postBitbucket(type, obj)
        //def bot = new TestBot()
        //def param = bot.room('hookTest')
        //bot.postChat(param.messages(result))
        assert result == messages
        // assert bot.messages[param] == messages
    }

}
