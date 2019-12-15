package net.zomis.duga.tasks

import net.zomis.duga.aws.Duga
import net.zomis.duga.aws.DugaMessage

class MessageTask(private val room: String, private val message: String) : DugaTask {

    override fun perform() {
        Duga().send(DugaMessage(room, message))
    }

}
