package net.zomis.duga.tasks

import net.zomis.duga.aws.DugaMessage

interface DugaTask {
    fun perform(): List<DugaMessage>
}
