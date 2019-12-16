package net.zomis.duga.hooks

import com.fasterxml.jackson.databind.JsonNode

interface DugaWebhook {

    fun handle(body: JsonNode): List<String>

}
