package net.zomis.duga.utils.github

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import org.slf4j.LoggerFactory

class GitHubApi(val client: HttpClient, gitHubKey: String?) {

    private val authHeader = gitHubKey?.let { "Authorization" to "token $it" }
    private val mapper = jacksonObjectMapper()
    private val logger = LoggerFactory.getLogger(javaClass)

    private suspend fun apiCall(url: String, page: Int = 1): JsonNode {
        val response = client.get<String>("https://api.github.com/$url?page=$page") {
            authHeader?.also { headers.append(it.first, it.second) }
        }
        return mapper.readTree(response)
    }

    data class GitHubCommitDetails(val additions: Int, val deletions: Int)

    suspend fun stars(repository: String): Int {
        val repo = apiCall("repos/$repository")
        return repo["stargazers_count"].asInt()
    }

    suspend fun commitDetails(repository: JsonNode, commit: JsonNode): GitHubCommitDetails? {
        val repoName = repository.text("full_name")
        val sha = if (commit.has("sha")) commit.text("sha") else commit.text("id")
        return try {
            val json = apiCall("repos/$repoName/commits/$sha")
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
