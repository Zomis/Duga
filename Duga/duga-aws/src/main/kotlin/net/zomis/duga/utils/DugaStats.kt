package net.zomis.duga.utils

import com.fasterxml.jackson.databind.JsonNode
import net.zomis.duga.tasks.StatisticsTask

class DugaStats {

    private fun stats(json: JsonNode, stats: Map<String, Int>) {
        val name = json.text("repository.full_name")
        val url = json.text("repository.html_url")
        StatisticsTask("", false).repository(name, url, stats)
    }

    fun addIssue(jsonNode: JsonNode, opened: Int) {
        if (opened > 0) {
            stats(jsonNode, mapOf("issues opened" to opened))
        } else {
            stats(jsonNode, mapOf("issues closed" to -opened))
        }
    }

    fun addIssueComment(jsonNode: JsonNode) {
        stats(jsonNode, mapOf("issue comments" to 1))
    }

    fun addCommits(jsonNode: JsonNode, commits: List<JsonNode>) {
        val details = commits.map {commit ->
            GitHubAPI().commitDetails(jsonNode["repository"], commit)
        }.filterNotNull()
        val commitCount = commits.size
        val additions = details.sumBy { it.additions }
        val deletions = details.sumBy { it.deletions }
        stats(jsonNode, mapOf("commits" to commitCount, "additions" to additions, "deletions" to deletions))
    }

}
