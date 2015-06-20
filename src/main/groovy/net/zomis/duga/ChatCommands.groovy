package net.zomis.duga

import net.zomis.duga.tasks.ChatMessageIncoming

import java.util.function.Consumer

class ChatCommands {

    private final List<Consumer<ChatMessageIncoming>> consumers = new ArrayList<>()
    private final DugaTasks tasks
    private final DugaBot bot

    ChatCommands(DugaChatListener bean) {
        this.tasks = bean.tasks
        this.bot = bean.chatBot
        consumers << {ChatMessageIncoming event ->
            if (event.content.contains('ping')) {
                event.reply('pong')
            }
        }
        consumers << {ChatMessageIncoming event ->
            if (event.content.contains('webhooks')) {
                User.withNewSession { status ->
                    User user = User.findByChatId(event.user_id)
                    if (user) {
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

                        event.ping('Hooked repos: ' + reposHooked)
                        event.ping('Unhooked repos: ' + unhooked)
                    } else {
                        event.reply('You are not a recognized user')
                    }
                }
            }
        }
        consumers << {ChatMessageIncoming event ->
            def command = 'add hook'
            int index = event.content.indexOf(command)
            if (index != -1) {
                String str = event.content.substring(index + command.length() + 1)
                User.withNewSession { status ->
                    User user = User.findByChatId(event.user_id)
                    if (user) {
                        List<String> reposHooked = new ArrayList<>()
                        List<String> unhooked = new ArrayList<>()
                        def request = [name: 'web', active: true, 'events': ['*'],
                            config: [
                                url: "http://stats.zomis.net/GithubHookSEChatService/hook?roomId=$event.room_id",
                                content_type: "json"
                            ]
                        ]
                        def repos = user.githubPost("repos/$user.githubName/$str/hooks", request)
                        event.ping('Result: ' + repos)
                    } else {
                        event.reply('You are not a recognized user')
                    }
                }
            }
        }
        consumers << {ChatMessageIncoming event ->
            if (event.content.contains('add stats')) {
                DailyInfo.withNewSession { status ->
                    def info = new DailyInfo()
                    info.comment = 'Created on command'
                    info.name = 'ABC' + Math.random()
                    info.url = 'http://www.example.com'
                    if (!info.save(failOnError: true, flush: true)) {
                        event.reply('Failed')
                        info.errors.each {
                            println it
                        }
                    } else {
                        event.reply('OK')
                    }
                }
            }
        }
        consumers << {ChatMessageIncoming event ->
            if (event.content.contains('add config')) {
                DailyInfo.withNewSession { status ->
                    def config = new DugaConfig()
                    config.key = 'ABC' + Math.random()
                    config.value = 'Created on command'
                    if (!config.save(failOnError: true, flush: true)) {
                        event.reply('Failed')
                        config.errors.each {
                            println it
                        }
                    } else {
                        event.reply('OK')
                    }
                }
            }
        }
        consumers << {ChatMessageIncoming event ->
            if (event.content.contains('add follow')) {
                Followed.withNewSession { status ->
                    def info = new Followed()
                    info.lastEventId = 0
                    info.name = 'ABC' + Math.random()
                    info.interestingEvents = '*'
                    info.lastChecked = 0
                    info.followType = 1
                    info.roomIds = '20298'
                    if (!info.save(failOnError: true, flush: true)) {
                        event.reply('Failed')
                        info.errors.each {
                            println it
                        }
                    } else {
                        event.reply('OK')
                    }
                }
            }
        }
        consumers << {ChatMessageIncoming message ->
            def command = 'task do'
            int index = message.content.indexOf(command)
            if (index != -1) {
                String str = message.content.substring(index + command.length() + 1)
                tasks.createTask(str).run()
                message.reply('OK')
            }
        }
        consumers << {ChatMessageIncoming message ->
            def command = 'task reload'
            int index = message.content.indexOf(command)
            if (index != -1) {
                def loadedTasks = tasks.reloadAll()
                message.reply(loadedTasks.size() + ' reloaded')
            }
        }
        consumers << {ChatMessageIncoming event ->
            if (event.content.contains('create task')) {
                TaskData.withNewSession { status ->
                    println 'Transaction ' + status
                    def task = new TaskData()
                    task.taskValue = 'no task defined'
                    task.cronStr = '0 0 * * * *'
                    if (!task.save(failOnError: true, flush: true)) {
                        event.reply('Failed')
                        task.errors.each {
                            println it
                        }
                    } else {
                        event.reply('OK')
                    }
                    println 'Posted OK'
                }
                println 'Done'
            }
        }
    }

    def botCommand(ChatMessageIncoming messageEvent) {
        for (Consumer consumer : consumers) {
            consumer.accept(messageEvent)
        }
    }
}
