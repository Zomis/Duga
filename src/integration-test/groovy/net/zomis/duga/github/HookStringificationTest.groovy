package net.zomis.duga.github

import net.zomis.duga.HookStringification
import org.junit.Test

class HookStringificationTest {

    @Test
    void testCodecovBotName() {
        assert "[**codecov\\[bot\\]**](https://github.com/apps/codecov)" == HookStringification.user(
            [login:"codecov[bot]",
             html_url: "https://github.com/apps/codecov"]
        )
    }

}
