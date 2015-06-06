package net.zomis.duga.tasks

import com.gistlabs.mechanize.Resource
import com.gistlabs.mechanize.document.json.JsonDocument
import com.gistlabs.mechanize.document.json.node.JsonNode
import groovy.json.JsonSlurper
import net.zomis.duga.DugaBot
import net.zomis.duga.TaskData
import net.zomis.duga.chat.WebhookParameters

class ListenTask implements Runnable {

    private final DugaBot bot
    private final String room
    private final WebhookParameters params
    private long lastHandledId

    public ListenTask(DugaBot bot, String room) {
        this.bot = bot
        this.room = room
        this.params = WebhookParameters.toRoom(room)
    }

    synchronized void latestMessages() {
        def agent = bot.agent()

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fkey", bot.fkey());
        parameters.put("mode", "messages");
        parameters.put("msgCount", String.valueOf(1));
        Resource response = agent.post("http://chat.stackexchange.com/chats/" + room + "/events", parameters)
        if (response instanceof JsonDocument) {
            def jsonDocument = response as JsonDocument
            JsonNode node = jsonDocument.root
            def json = new JsonSlurper().parseText(node.toString())
            def events = json.events
            for (def event in events) {
                if (event.message_id < lastHandledId) {
                    continue
                }
                if (event.user_id == 98071) {
                    def content = event.content
                    if (content.startsWith('@Duga')) {
                        println "possible command: $content"
                        if (content.contains('create task')) {
                            TaskData.withNewSession { status ->
                                println 'Transaction ' + status
                                def task = new TaskData()
                                task.taskValue = 'no task defined'
                                task.cronStr = '0 0 * * * *'
                                if (!task.save(failOnError: true, flush: true)) {
                                    bot.postSingle(params, ":$event.message_id Failed")
                                    task.errors.each {
                                        println it
                                    }
                                } else {
                                    bot.postSingle(params, ":$event.message_id OK")
                                }
                                println 'Posted OK'
                            }
                            println 'Done'
                        }
                    }
                }
                lastHandledId = event.message_id
            }
/*            Root node: {"ms":4,"time":41194973,"sync":1433551091,"events":
                [{"room_id":16134,"event_type":1,"time_stamp":1433547911,"user_id":125580,"user_name":"Duga","message_id":22039309,"content":"Loki Astari vs. Simon Andr&#233; Forsberg: 4383 diff. Year: -1368. Quarter: -69. Month: -5. Week: +60. Day: -25."}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433548817,"user_id":125580,"user_name":"Duga","message_id":22039366,"content":"<b><i>RELOAD!<\/i><\/b>"}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433548849,"user_id":125580,"user_name":"Duga","message_id":22039371,"content":"&#91;<a href=\"https://github.com/retailcoder/Rubberduck\" rel=\"nofollow\"><b>retailcoder/Rubberduck<\/b><\/a>&#93; 12 commits. 2 closed issues. 5 issue comments."}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433551821,"user_id":98071,"user_name":"Simon André Forsberg","parent_id":22039371,"show_parent":true,"message_id":22039768,"content":"@Duga No open issues today?"}]}

        Success: {"id":22039802,"time":1433552063}
*/
        }
    }

    @Override
    void run() {
        latestMessages()
    }
}
