package net.zomis.duga.tasks

import net.zomis.duga.aws.Duga
import net.zomis.duga.aws.DugaMessage
import java.time.Instant

class MessageTask(private val room: String, private val message: String) : DugaTask {

    override fun perform() {
        val msg = message.replace("%time%", Instant.now().toString())
        Duga().send(DugaMessage(room, msg))
    }

}
