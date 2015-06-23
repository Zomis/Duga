package net.zomis.duga.tasks

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import net.zomis.duga.DugaChatListener
import net.zomis.duga.User
import net.zomis.duga.chat.WebhookParameters

import java.util.concurrent.Callable

/**
 * Delegate for running chat commands
 */
abstract class ChatCommandDelegate extends Script {

    private ChatMessageIncoming message
    private DugaChatListener bean

    ChatCommandDelegate() {
        // this constructor intentionally left blank
    }

    void init(ChatMessageIncoming message, DugaChatListener bean) {
        if (this.message || this.bean) {
            throw new IllegalStateException('message and bean can only be initialized once')
        }
        this.message = message
        this.bean = bean
    }

    Closure ping = {
        allowAll()
        message.reply('pong!')
    }

    void allowAll() {
        assert message.user_id > 0
    }

    Map say(String text) {
        requireAdmin()
        [inRoom: {int id ->
            bean.chatBot.postSingle(WebhookParameters.toRoom(Integer.toString(id)), text)
        }, default: {
            message.reply(text)
        }]
    }

    void requireUser() {
        assert message.fetchUser()
    }

    void requireRole(String role) {
        User user = message.fetchUser()
        if (!user) {
            message.reply('You are not registered with Duga. Please go to http://stats.zomis.net/GithubHookSEChatService/registration/index for registration instructions')
            assert false
        }
        boolean userHasRole = false
        User.withNewSession {status ->
            userHasRole = message.fetchUser().getAuthorities().stream().anyMatch({auth ->
                auth.authority.equals(role)
            })
            if (!userHasRole) {
                message.reply('Unauthorized, requires ' + role)
            }
        }
        assert userHasRole
    }

    void requireAdmin() {
        requireRole('ROLE_ADMIN')
    }

    ChatMessageIncoming getMessage() {
        message
    }

    DugaChatListener getBean() {
        bean
    }

    def register(String githubKey) {
        allowAll()
        User.withNewSession {status ->
            User user = User.findByPingExpect(githubKey)
            if (user == null) {
                message.reply('No such user found.')
            } else if (user.accountLocked) {
                user.setAccountLocked(false)
                user.setChatName(message.user_name)
                user.chatId = message.user_id
                if (!user.save(failOnError: true, flush: true)) {
                    message.reply('Unable to save')
                    user.errors.each {
                        message.reply it
                    }
                } else {
                    message.reply('You have been registered!')
                }
            } else {
                message.reply('Ping Expect is already taken.')
            }
        }
    }

}
