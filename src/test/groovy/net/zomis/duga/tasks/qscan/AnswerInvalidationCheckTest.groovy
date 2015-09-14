package net.zomis.duga.tasks.qscan

import org.junit.Test

import static net.zomis.duga.tasks.qscan.AnswerInvalidationCheck.formatDisplayName

class AnswerInvalidationCheckTest {
    @Test
    public void format_name_with_apostrophe() {
        def displayName = "Mat&#39;s Mug"
        def expected = "Mat's Mug"
        assert expected == formatDisplayName(displayName)
    }
}
