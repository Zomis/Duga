package net.zomis.duga.tasks

import net.zomis.duga.DugaChatListener
import net.zomis.duga.User
import net.zomis.duga.chat.WebhookParameters

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

    Closure webhooks = {
        requireUser()
        User user = message.fetchUser()

        List<String> reposHooked = new ArrayList<>()
        List<String> unhooked = new ArrayList<>()
        def repos = user.github('user/repos')
        for (def repo in repos) {
            if (repo.owner.login == user.githubName) {
                // make request for webhooks
                def hooks = user.github('repos/' + repo.full_name + '/hooks')
                boolean hooked = false
                for (def hook in hooks) {
                    def url = hook?.config?.url
                    if (url?.contains('zomis')) {
                        hooked = true
                    }
                }
                if (hooked) {
                    reposHooked.add(repo.full_name)
                } else {
                    unhooked.add(repo.full_name)
                }
            }
        }
        message.reply('Hooked repos: ' + reposHooked)
        message.reply('Unhooked repos: ' + unhooked)
    }

    void addWebhook(String repo, int roomId) {
        requireUser()
        User user = message.fetchUser()
        def request = [name: 'web', active: true, 'events': ['*'],
            config: [
                url: "http://stats.zomis.net/GithubHookSEChatService/hook?roomId=$roomId",
                content_type: "json"
            ]
        ]
        def repos = user.githubPost("repos/$user.githubName/$repo/hooks", request)
        message.reply('Result: ' + repos)
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
