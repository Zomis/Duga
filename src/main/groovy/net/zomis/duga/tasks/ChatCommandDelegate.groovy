package net.zomis.duga.tasks

import net.zomis.duga.DugaChatListener
import net.zomis.duga.HookStringification
import net.zomis.duga.User
import net.zomis.duga.chat.listen.ChatMessageIncoming

import java.util.concurrent.atomic.AtomicReference

/**
 * Delegate for running chat commands
 */
abstract class ChatCommandDelegate extends Script {

    // DO NOT RENAME THESE FIELDS. Their names are denied through hardcoding
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

    private User fetchUser() {
        AtomicReference<User> user = new AtomicReference<>();
        User.withNewSession {status ->
            user.set(User.findByChatId(message.getUserId()))
        }
        return user.get()
    }

    Closure webhooks = {
        requireUser()
        User user = fetchUser()

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

    DugaLearning getProgrammers() {
        if (message.getParentId() > 0) {
            message.reply("Reply to the message you want to use and do `@Duga do programmers.features` " +
                "or `@Duga do programmers.classify true` " +
                "or `@Duga do programmers.classify false`")
        }
        String text = ChatScrape.fetch(message.getParentId());
        return new DugaLearning(null, text)
    }

    void addWebhook(String repo, int roomId) {
        requireUser()
        User user = fetchUser()
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
        assert message.getUserId() > 0
    }

    Map say(String text) {
        requireAdmin()
        [inRoom: {int id ->
            bean.chatBot.postSingle(bean.chatBot.room(Integer.toString(id)), text)
        }, default: {
            message.reply(text)
        }]
    }

    void requireUser() {
        assert fetchUser()
    }

    void requireRole(String role) {
        User user = fetchUser()
        if (!user) {
            message.reply('You are not registered with Duga. Please go to http://stats.zomis.net/GithubHookSEChatService/registration/index for registration instructions')
            assert false
        }
        boolean userHasRole = false
        User.withNewSession {status ->
            userHasRole = fetchUser().getAuthorities().stream().anyMatch({auth ->
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

    Map issue(String repo) {
        requireUser()
        [search: {String query ->
            // https://api.github.com/search/issues?q=systems%20repo:Cardshifter/Cardshifter%20is:open
            String q = query.replaceAll(' ', '+')
            def issues = fetchUser().github("search/issues?q=repo:$repo+is:open+$q")
            StringBuilder results = new StringBuilder(issues.total_count + ' results found. ' as String)
            int count = 0
            for (def json in issues.items) {
                String next = HookStringification.issue(json)
                if (results.length() + next.length() > 500) {
                    message.reply(results.toString())
                    results.setLength(0)
                }
                results.append(next)
                results.append(' ')
                if (++count >= 10) {
                    break;
                }
            }
            message.reply(results.toString())
        }, id: {int id ->
            def issue = fetchUser().github("repos/$repo/issues/$id")
            message.reply(HookStringification.issue(issue))
        }]
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

    void taskReload() {
        requireAdmin()
        def loadedTasks = bean.tasks.reloadAll()
        message.reply(loadedTasks.size() + ' reloaded')
    }

    void listen(int roomId) {
        requireAdmin()
        def listenTask = bean.listenStart(String.valueOf(roomId))
        message.reply('Listening in room ' + roomId)
    }

    void stop() {
        requireAdmin()
        ListenTask task = bean.listenStop(message.room_id)
        if (task) {
            message.post('TTQW! (Stopped listening)')
        } else {
            message.reply('Did not find task in map. This is probably a bug.')
        }
    }

}
