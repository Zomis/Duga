package net.zomis.duga.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test

class HookTest {

    val mapper = ObjectMapper()

    @Test
    fun test() {
        val node = mapper.createObjectNode()
        node.put("state", "failure")
        node.put("name", "Zomis/test")
        node.put("sha", "0123456789abcdef")
        node.set("branches", mapper.createArrayNode().add(
            mapper.createObjectNode().put("name", "master")
        ))
        node.put("description", "This commit looks bad")
        val result = HookString().postGithub("status", node)
        println(result)
    }

}