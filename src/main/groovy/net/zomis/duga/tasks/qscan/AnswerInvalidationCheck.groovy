package net.zomis.duga.tasks.qscan

import net.zomis.duga.chat.BotRoom
import net.zomis.duga.chat.ChatBot
import net.zomis.duga.StackAPI
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Instant
import java.util.stream.Collectors
import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml

class AnswerInvalidationCheck {

    private static final Logger logger = LoggerFactory.getLogger(AnswerInvalidationCheck.class);
    private static final BotRoom debug = BotRoom.toRoom('20298');

    static void perform(def result, Instant lastCheck, StackAPI stackExchangeAPI, ChatBot dugaBot, BotRoom params) {
        logger.debug('Answer invalidation check')
        List questions = result.items
        questions.each {
//            def created = it.creation_date
//            def activity = it.creation_date
            def edited = it.last_edit_date
            String questionLink = it.link
            String op = formatDisplayName(it.owner.display_name)
            int questionId = it.question_id
            if (edited >= lastCheck.epochSecond && it.answer_count > 0) {
                logger.info('edited: ' + it.question_id)
                int id = it.question_id
                def edits = stackExchangeAPI.apiCall(editCall(id), 'codereview', '!9YdnS7lAD')
                dugaBot.postAsync(debug.message("Edits fetched for $id: ${edits.items.size()}. quota remaining $edits.quota_remaining"))
                def possibleInvalidations = codeChanges(edits, lastCheck)
                if (!possibleInvalidations.isEmpty()) {
                    String link = questionLink.replaceAll('/questions/.*', "/posts/$questionId/revisions")
                    String editor = possibleInvalidations.stream()
                        .map({formatDisplayName(it.user.display_name)})
                        .collect(Collectors.joining(', '))
                    dugaBot.postAsync(params.message("*possible answer invalidation by $editor on question by $op:* $link"))
                }
            }
        }
    }

    static String formatDisplayName(String displayName) {
        return unescapeHtml(displayName)
    }

    static boolean codeChanged(def edits, Instant lastCheck) {
        return codeChanges(edits, lastCheck).size() > 0
    }

    static List<Object> codeChanges(def edits, Instant lastCheck) {
        List<Object> result = []
        edits.items.each {
            if (!it.last_body) {
                return;
            }
            if (it.creation_date < lastCheck.epochSecond) {
                return;
            }
            if (it.is_rollback) {
                return;
            }
            String code = stripNonCode(it.body)
            String codeBefore = stripNonCode(it.last_body)
            if (!code.equals(codeBefore)) {
                result.add(it)
            }
        }
        return result
    }

    static String editCall(int id) {
        "posts/$id/revisions"
    }

    static String stripNonCode(String post) {
        post = post.replaceAll('[\\t ]', '')
        int keepCount = 0
        int index = post.indexOf('<code>')
        while (index >= 0) {
            int endIndex = post.indexOf('</code>')
            assert endIndex >= 0
            String before = post.substring(0, keepCount)
            String code = post.substring(index + '<code>'.length(), endIndex)
            String after = post.substring(endIndex + '</code>'.length())
            if (code.contains('\\n') || code.contains('\n')) {
                post = before + code + after
                keepCount += code.length()
            } else {
                post = before + after
            }
            index = post.indexOf('<code>')
        }
        return post.substring(0, keepCount)
    }

}
