package net.zomis.duga.github

import groovy.json.JsonSlurper
import net.zomis.duga.DugaStats
import net.zomis.duga.HookStringification
import net.zomis.duga.chat.TestBot
import net.zomis.duga.chat.WebhookParameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
class GithubWebhookTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = []
        data << ['pull_request', 'payload-pr-opened',
            ['**\\[[Cardshifter/HTML-Client](https://github.com/Cardshifter/HTML-Client)\\]** ' +
             '[**SirPython**](https://github.com/SirPython) created pull request ' +
             '[**#98: Develop**](https://github.com/Cardshifter/HTML-Client/pull/98) to merge ' +
             '[**develop**](https://github.com/Cardshifter/HTML-Client/tree/develop) into ' +
             '[**deploy-script**](https://github.com/Cardshifter/HTML-Client/tree/deploy-script)',
             '> updating deploy-script']]
        data << ['release', 'payload-release-created',
            ['**\\[[Vannevelj/RoslynTester](https://github.com/Vannevelj/RoslynTester)\\]** ' +
             '[**Vannevelj**](https://github.com/Vannevelj) published release ' +
             '[**v1.5.0**](https://github.com/Vannevelj/RoslynTester/releases/tag/v1.5.0)']]
        return data;
    }

    @Parameterized.Parameter(0)
    public List data

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
        assert data.size() == 3
        String type = data.get(0)
        String file = data.get(1) + '.json'
        List<String> messages = data.get(2) as List<String>
        def stream = getClass().classLoader.getResourceAsStream(file)
        assert stream : "No stream found for '$file'"
        def obj = new JsonSlurper().parseText(stream.text)
        def result = stringer.postGithub(type, obj)
        def bot = new TestBot()
        def param = WebhookParameters.toRoom('hookTest')
        bot.postChat(param.messages(result))
        assert bot.messages[param] == messages
    }

}
