package net.zomis.duga.manual
/*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.jsonBody
import java.io.File
import java.util.Scanner

class GitHubWebhook(private val repository: String, jsonNode: JsonNode) {

    val url: String = jsonNode["url"].asText()
    private val events: List<String> = jsonNode["events"].map { it.asText() }
    private val configDomain: String = jsonNode["config"]?.get("domain")?.asText() ?: ""
    val destinationUrl: String = jsonNode["config"]?.get("url")?.asText() ?: ""
    private val contentType: String = jsonNode["config"]?.get("content_type")?.asText() ?: ""
    private val insecureSSL: String = jsonNode["config"]?.get("insecure_ssl")?.asText() ?: ""

    override fun toString(): String {
        return "GitHubWebhook(repository='$repository', url='$url', events=$events, configDomain=$configDomain, destinationUrl=$destinationUrl, contentType=$contentType, insecureSSL=$insecureSSL)"
    }

    fun isOldDugaHook(): Boolean {
        return this.destinationUrl.contains("zomis.net") && this.destinationUrl.contains("GithubHookSEChatService")
    }

    fun isDugaHook(): Boolean {
        if (this.destinationUrl.startsWith("https://duga.zomis.net/github")) {
            return true
        }
        return this.destinationUrl.contains("zomis.net") && this.destinationUrl.contains("GithubHookSEChatService")
    }

    fun isNewDugaHook(): Boolean {
        return this.destinationUrl.startsWith("https://duga.zomis.net/github")
    }

}

class HookEditor(token: String) {

    private val mapper = ObjectMapper()
    private val authHeader = "Authorization" to "token $token"

    private fun apiCall(url: String, page: Int = 1): Pair<JsonNode, Response> {
        val response = Fuel.get("https://api.github.com/$url?page=$page")
            .header(authHeader)
            .responseString()
        val tree = mapper.readTree(response.third.get())
        return tree to response.second
    }

    fun listMyRepos(page: Int = 1): List<String> {
        return sequence {
            val repos = apiCall("user/repos", page)
            val reposAndAdmin = repos.first.mapNotNull {repo ->
                val adminPermission = repo["permissions"]["admin"].asBoolean()
                val name = repo["full_name"].asText()
                return@mapNotNull name to adminPermission
            }
//        reposAndAdmin.filter { !it.second }.map { it.first }.forEach { println("Not admin for $it") }
            yieldAll(reposAndAdmin.filter { it.second }.map { it.first })
            if (repos.second.header("Link").any { it.contains("rel=\"next\"") }) {
                yieldAll(listMyRepos(page + 1))
            }
        }.toList()
    }

    fun fixDugaWebhook(roomId: String, repository: String, existingHook: GitHubWebhook?) {
        println("Fix Duga Webhook: $repository. Existing hook is ${existingHook?.destinationUrl}")

        val json = mapOf(
            "active" to true,
            "events" to arrayOf("*"),
            "config" to mapOf(
                "url" to "https://duga.zomis.net/github?roomId=$roomId",
                "content_type" to "json",
                "insecure_ssl" to "0"
            )
        )
        if (existingHook != null) {
            val response = Fuel.patch(existingHook.url)
                .jsonBody(mapper.writeValueAsString(json))
                .header(authHeader).responseString()
            println(response.third.get())
        } else {
            val response = Fuel.post("https://api.github.com/repos/$repository/hooks")
                .header(authHeader)
                .jsonBody(mapper.writeValueAsString(json.plus("name" to "web")))
                .responseString()
            println(response.third.get())
        }
    }

    fun listWebhooks(repository: String): List<GitHubWebhook> {
        val hooks = apiCall("repos/$repository/hooks")
        return hooks.first.map { GitHubWebhook(repository, it) }
    }

}

fun main(args: Array<String>) {
    val str = File("c:/Users/Simon/Desktop/duga-hook.key").readText(Charsets.UTF_8).trim()
    val hookEditor = HookEditor(str)
    val repos = hookEditor.listMyRepos()
    val scanner = Scanner(System.`in`)
//    val repos = arrayOf("Zomis/test")
    repos.forEach {repo ->
        println(repo)
        val hooks = hookEditor.listWebhooks(repo)
        hooks.forEach { println("  $it") }
        val dugaHook = hooks.find { it.isDugaHook() }
        if (hooks.none { it.isNewDugaHook() } && scanner.nextLine() == "y") {
            hookEditor.fixDugaWebhook("16134", repo, dugaHook)
        }
    }
    scanner.close()
}
*/