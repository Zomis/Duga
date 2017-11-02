package net.zomis.duga

import net.zomis.duga.chat.listen.ChatMessageIncoming
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.Consumer

class ChatCommands {

    private static final Logger logger = LoggerFactory.getLogger(ChatCommands.class)

    private final List<Consumer<ChatMessageIncoming>> consumers = new ArrayList<>()
    private final DugaTasks tasks
    private final DugaBotService bot

    ChatCommands(DugaChatListener bean) {
        this.tasks = bean.tasks
        this.bot = bean.chatBot
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
                            logger.error(it)
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
                            logger.error(it)
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
                            logger.error(it)
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
        consumers << {ChatMessageIncoming event ->
            if (event.content.contains('create task')) {
                TaskData.withNewSession { status ->
                    logger.info('Transaction ' + status)
                    def task = new TaskData()
                    task.taskValue = 'no task defined'
                    task.cronStr = '0 0 * * * *'
                    if (!task.save(failOnError: true, flush: true)) {
                        event.reply('Failed')
                        task.errors.each {
                            logger.error(it)
                        }
                    } else {
                        event.reply('OK')
                    }
                    logger.info('Posted OK')
                }
                logger.info('Done')
            }
        }
    }

    def botCommand(ChatMessageIncoming messageEvent) {
        for (Consumer consumer : consumers) {
            consumer.accept(messageEvent)
        }
    }
}
