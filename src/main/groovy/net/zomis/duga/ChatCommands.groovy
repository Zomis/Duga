package net.zomis.duga

import net.zomis.duga.chat.WebhookParameters

import java.util.function.Consumer

class ChatCommands {

    private final List<Consumer<Object>> consumers = new ArrayList<>()
    private final DugaTasks tasks
    private final DugaBot bot

    ChatCommands(DugaTasks tasks, DugaBot bot) {
        this.tasks = tasks
        this.bot = bot
        consumers << {Object event ->
            String str = event.content
            String room = event.room_id
            if (str.contains('create task')) {
                TaskData.withNewSession { status ->
                    println 'Transaction ' + status
                    def task = new TaskData()
                    task.taskValue = 'no task defined'
                    task.cronStr = '0 0 * * * *'
                    if (!task.save(failOnError: true, flush: true)) {
                        bot.postSingle(WebhookParameters.toRoom(room), ":$event.message_id Failed")
                        task.errors.each {
                            println it
                        }
                    } else {
                        bot.postSingle(WebhookParameters.toRoom(room), ":$event.message_id OK")
                    }
                    println 'Posted OK'
                }
                println 'Done'
            }
        }
    }

    def botCommand(def messageEvent) {
        for (Consumer consumer : consumers) {
            consumer.accept(messageEvent)
        }
    }
}
