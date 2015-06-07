package net.zomis.duga

import net.zomis.duga.chat.WebhookParameters
import net.zomis.duga.tasks.ChatMessageIncoming

import java.util.function.Consumer

class ChatCommands {

    private final List<Consumer<ChatMessageIncoming>> consumers = new ArrayList<>()
    private final DugaTasks tasks
    private final DugaBot bot

    ChatCommands(DugaTasks tasks, DugaBot bot) {
        this.tasks = tasks
        this.bot = bot
        consumers << {ChatMessageIncoming event ->
            String str = event.content
            String room = event.room_id
            if (str.contains('create task')) {
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
