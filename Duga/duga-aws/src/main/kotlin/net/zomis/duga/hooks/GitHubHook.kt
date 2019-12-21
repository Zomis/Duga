package net.zomis.duga.hooks

import com.fasterxml.jackson.databind.JsonNode
import net.zomis.duga.utils.HookString

class GitHubHook(private val githubEventType: String) : DugaWebhook {

    override fun handle(body: JsonNode): List<String> {
        return HookString().postGithub(githubEventType, body)
    }

}
