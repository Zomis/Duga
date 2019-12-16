package net.zomis.duga.hooks

import com.fasterxml.jackson.databind.JsonNode

class SplunkHook : DugaWebhook {

    override fun handle(body: JsonNode): List<String> {
        val splunkFields: (JsonNode) -> String = {node ->
            node.fields().asSequence().associate { it.key to it.value }.map { "${it.key}: ${it.value.asText()}" }.joinToString(", ")
        }
        val node = body["result"]
        return listOf("@SimonForsberg **Splunk Alert:** ${body["search_name"]} - ${splunkFields(node)}")
    }

}