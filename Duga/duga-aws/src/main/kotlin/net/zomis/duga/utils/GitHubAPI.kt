package net.zomis.duga.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import org.slf4j.LoggerFactory

data class GitHubCommitDetails(val additions: Int, val deletions: Int)

class GitHubAPI {

    private val githubAPI = System.getenv("GITHUB_API")
    private val authHeader = "Authorization" to "token $githubAPI"
    private val mapper = ObjectMapper()
    private val logger = LoggerFactory.getLogger(javaClass)

    private fun apiCall(url: String, page: Int = 1): Pair<JsonNode, Response> {
        val response = Fuel.get("https://api.github.com/$url?page=$page")
            .header(authHeader)
            .responseString()
        val tree = mapper.readTree(response.third.get())
        return tree to response.second
    }

    fun commitDetails(repository: JsonNode, commit: JsonNode): GitHubCommitDetails? {
        val repoName = repository.text("full_name")
        val sha = if (commit.has("sha")) commit.text("sha") else commit.text("id")
        return try {
            val result = apiCall("repos/$repoName/commits/$sha")
            val json = result.first
            val additions = json["stats"]["additions"].asInt()
            val deletions = json["stats"]["deletions"].asInt()
            logger.info("Adding {} additions and {} deletions to {} for commit {}", additions, deletions, repoName, sha)
            GitHubCommitDetails(additions, deletions)
        } catch (ex: Exception) {
            logger.warn("Can't get commit details for ${repository.text("full_name")} $sha", ex)
            null
        }
    }

}