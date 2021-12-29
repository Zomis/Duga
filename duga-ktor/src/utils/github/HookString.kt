package net.zomis.duga.utils.github

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.zomis.duga.utils.stats.DugaStats
import org.slf4j.LoggerFactory

fun JsonNode.text(path: String): String {
    val paths = path.split(".")
    return paths.fold(this) {
        curr, next -> if (curr.has(next)) curr.get(next) else throw IllegalArgumentException("No such path found $path")
    }.asText()
}

fun JsonNode.textIf(path: String): String? {
    val paths = path.split(".")
    val start: JsonNode? = this
    return paths.fold(start) {curr, next ->
        if (curr?.has(next) == true) curr.get(next) else null
    }?.asText()
}

private const val MAX_DISTINCT_COMMITS = 10
class HookString(
    private val stats: DugaStats,
    private val gitHubApi: GitHubApi,
    private val scope: CoroutineScope
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private fun substr(str: String, idx: Int, len: Int): String {
        var index = idx
        var length = len
        if (index < 0) {
            index += str.length
        }

        length += if (length < 0) str.length else index

        if (index > str.length) {
            return ""
        }
        length = Math.min(length, str.length)

        return str.substring(index, length)
    }

    private fun substr(str: String, index: Int): String {
        if (index >= 0) {
            return substr(str, index, str.length - index)
        } else {
            return substr(str, index, -index)
        }
    }

    val TRUNCATE_TARGET = 498; // max chars in a message is 500, there's two chars in the front of the truncated string

    private fun truncate(string: String): String {
        return substr(string, 0, TRUNCATE_TARGET)
    }

    fun repository(json: JsonNode): String {
        if (!json.has("repository")) {
            return ""
        }
        return "**\\[[${json.text("repository.full_name")}](${json.text("repository.html_url")})]**"
    }

    fun format(obj: JsonNode, str: String): String {
        // Using replaceAll here makes a mess in regex escaping. Avoid using the same %thing% twice in templates.
        return str.replace("%repository%", repository(obj))
            .replace("%sender%", user(obj["sender"]))
    }

    fun issue(json: JsonNode): String {
        return "[**#${json.text("number")}: ${json.text("title").trim()}**](${json.text("html_url")})"
    }

    fun labelJson(json: JsonNode): String {
        return "[**${json.text("label.name")}**](${json.text("repository.html_url")}/labels/${json.text("label.name").replace(" ", "%20")})"
    }

    fun user(json: JsonNode?): String {
        if (json == null) {
            return ""
        }
        var username = json.text("login")
        username = username.replace("[", "\\[")
        username = username.replace("]", "\\]")
        return "[**$username**](${json.text("html_url")})"
    }

    fun ping(result: MutableList<String>, json: JsonNode) {
        result.add(format(json, "%repository% Ping: ${json.text("zen")}"))
    }

    fun sender(json: JsonNode): String {
        return "[**${json.text("sender.login")}**](${json.text("sender.html_url")})"
    }

    fun commit_comment(result: MutableList<String>, json: JsonNode) {
        val path = json.textIf("comment.path")
        val commitId = json.text("comment.commit_id").substring(0, 8)
        val commitLink = "[$commitId](${json.text("repository.html_url")}/commit/${json.text("comment.commit_id")})"
        if (path.isNullOrEmpty()) {
            result.add(format(json, "%repository% %sender% [commented](${json.text("comment.html_url")}) on commit $commitLink"))
        } else {
            result.add(format(json, "%repository% %sender% [commented on ${json.text("comment.path")}](${json.text("comment.html_url")}) of commit $commitLink"))
        }
        result.add("> " + truncate(json.text("comment.body")))
    }

    fun create(result: MutableList<String>, json: JsonNode) {
        var refUrl: String? = null
        when (json.text("ref_type")) {
            "branch" -> refUrl = json.text("repository.html_url") + "/tree/" + json.text("ref")
            "tag" -> refUrl = json.text("repository.html_url") + "/releases/tag/" + json.text("ref")
            "repository" -> {
                result.add(format(json, "%repository% %sender% created ${json.text("ref_type")}"))
                return
            }
        }
        result.add(format(json, "%repository% %sender% created ${json.text("ref_type")} [**${json.text("ref")}**]($refUrl)"))
    }

    fun release(result: MutableList<String>, json: JsonNode) {
        val prerelease: Boolean = json["release"]["prerelease"].asBoolean()
        var release = if (json["release"]["draft"].asBoolean()) "draft " else ""
        release += if (prerelease) "prerelease" else "release"
        val refUrl = json.text("release.html_url")
        result.add(format(json, "%repository% %sender% ${json.text("action")} $release [**${json.text("release.tag_name")}**]($refUrl)"))
    }

    fun delete(result: MutableList<String>, json: JsonNode) {
        result.add(format(json, "%repository% %sender% deleted ${json.text("ref_type")} **${json.text("ref")}**"))
    }

    fun fork(result: MutableList<String>, json: JsonNode) {
        result.add(format(json, "%repository% %sender% forked us into [**${json.text("forkee.full_name")}**](${json.text("forkee.html_url")})"))
    }

    fun gollum(result: MutableList<String>, json: JsonNode) {
        for (page in json["pages"]) {
            result.add(format(json, "%repository% %sender% ${page.text("action")} wiki page [**${page.text("title").trim()}**](${page.text("html_url")})"))
        }
    }

    fun issues(result: MutableList<String>, json: JsonNode) {
        val issue = "[**#${json.text("issue.number")}: ${json.text("issue.title").trim()}**](${json.text("issue.html_url")})"
        var extra = ""
        if (json.has("assignee")) {
            extra = "[**${json.text("assignee.login")}**](${json.text("assignee.html_url")})"
        }
        if (json.has("label")) {
            extra = labelJson(json)
        }
        val action = json.text("action")
        when (action) {
            "assigned" -> result.add(format(json, "%repository% %sender% $action $extra to issue $issue"))
            "unassigned" -> result.add(format(json, "%repository% %sender% $action $extra from issue $issue"))
            "labeled" -> result.add(format(json, "%repository% %sender% added label $extra to issue $issue"))
            "unlabeled" -> result.add(format(json, "%repository% %sender% removed label $extra from issue $issue"))
            "opened" -> {
                result.add(format(json, "%repository% %sender% opened issue $issue"))
                val issueBody = json.textIf("issue.body")
                if (issueBody != null && issueBody.isNotEmpty()) {
                    result.add("> " + truncate(issueBody))
                }
                stats.addIssue(json, 1)
            }
            "closed" -> {
                result.add(format(json, "%repository% %sender% closed issue $issue"))
                stats.addIssue(json, -1)
            }
            "reopened" -> {
                result.add(format(json, "%repository% %sender% reopened issue $issue"))
                stats.addIssue(json, 1)
            }
            else -> result.add(format(json, "%repository% %sender% $action issue $issue"))
        }
    }

    fun issue_comment(result: MutableList<String>, json: JsonNode) {
        val issue = issue(json["issue"])
        val commentTarget = if (!json["issue"].has("pull_request")) "issue" else "pull request"
        result.add(format(json, "%repository% %sender% ${json.text("action")} [comment](${json.text("comment.html_url")}) on $commentTarget $issue"))
        result.add("> " + truncate(json.text("comment.body")))
        stats.addIssueComment(json)
    }

    fun label(result: MutableList<String>, json: JsonNode) {
        result.add(format(json, "%repository% %sender% ${json.text("action")} label ${labelJson(json)}"))
    }

    fun repository(result: MutableList<String>, json: JsonNode) {
        result.add(format(json, "%repository% %sender% ${json.text("action")} repository"))
    }

    fun project_card(result: MutableList<String>, json: JsonNode) {
        val options = arrayOf(
                "%repository% %sender% ${json.textIf("action")} project card",
                "%repository% %sender% did something with some project card",
                "%repository% %sender% is playing around with a project card",
                "%repository% %sender% is checking what fun stuff @Duga can say about project cards",
                "%repository% %sender% is bored so why not move a project card",
                "%repository% %sender% project card. Enough said."
        )
        result.add(format(json, options.random()))
    }

    fun status(result: MutableList<String>, json: JsonNode) {
        if (json.text("state") == "pending") {
            logger.info("Status pending.")
            return
        }
        val repoURL = "https://github.com/${json.text("name")}"
        val commitId = json.text("sha").substring(0, 8)
        var branch: String? = null
        if (json["branches"].size() > 0) {
            branch = json["branches"][0].text("name")
        }
        val targetUrl = json.textIf("target_url")
        val build = if (!targetUrl.isNullOrEmpty()) "[**build**](${json.text("target_url")})" else "build"

        branch = if (branch == null) "unknown branch" else "[**$branch**]($repoURL/tree/$branch)"
        val mess = "**\\[[${json.text("name")}]($repoURL)\\]** " +
                "$build for commit " +
                "[**$commitId**]($repoURL/commit/$commitId) " +
                "on $branch: ${json.text("description")}"
        result.add(mess)
        val state = json.text("state")
        if (state != "pending" && state != "success") {
            result.add("**BUILD FAILURE!**")
        }
    }

    fun member(result: MutableList<String>, json: JsonNode) {
        result.add(format(json, "%repository% %sender% ${json.text("action")} [**${json.text("member.login")}**](${json.text("member.html_url")})"))
    }

    fun pull_request_review_comment(result: MutableList<String>, json: JsonNode) {
        result.add(format(json, "%repository% %sender% [commented on **${json.text("comment.path")}**](${json.text("comment.html_url")}) of pull request ${issue(json["pull_request"])}"))
    }

    fun pull_request(result: MutableList<String>, json: JsonNode) {
        val head = json["pull_request"]["head"]
        val base = json["pull_request"]["base"]
        val pr = issue(json["pull_request"])
        val assignee = user(json["assignee"])

        val headText: String?
        val baseText: String?
        if (head.get("repo") == base.get("repo")) {
            headText = head.text("ref")
            baseText = base.text("ref")
        } else {
            headText = head.text("repo.full_name") + "/" + head.text("ref")
            baseText = base.text("repo.full_name") + "/" + base.text("ref")
        }

        val headStr = "[**$headText**]($head.repo.html_url/tree/$head.ref)"
        val baseStr = "[**$baseText**]($base.repo.html_url/tree/$base.ref)"
        val label = if (json.has("label"))
            "[**${json.text("label.name")}**](${json.text("repository.html_url")}/labels/${json.text("label.name").replace(" ", "%20")})" else ""
        val action = json.text("action")
        when (action) {
            "assigned" -> result.add(format(json, "%repository% %sender% assigned $assignee to pull request $pr"))
            "unassigned" -> result.add(format(json, "%repository% %sender% unassigned $assignee from pull request $pr"))
            "labeled" -> result.add(format(json, "%repository% %sender% added label $label to pull request $pr"))
            "unlabeled" -> result.add(format(json, "%repository% %sender% removed label $label from pull request $pr"))
            "opened" -> {
                result.add(format(json, "%repository% %sender% created pull request $pr to merge $headStr into $baseStr"))
                val body = json.textIf("pull_request.body")
                if (!body.isNullOrEmpty()) {
                    val prBody = truncate(body)
                    result.add("> $prBody")
                }
            }
            "closed" -> {
                if (json["pull_request"].get("merged").asBoolean()) {
                    result.add(format(json, "%repository% %sender% merged pull request $pr from $headStr into $baseStr"))
                } else {
                    result.add(format(json, "%repository% %sender% rejected pull request $pr"))
                }
            }
            "reopened" -> result.add(format(json, "%repository% %sender% reopened pull request $pr"))
            "synchronize" -> result.add(format(json, "%repository% %sender% synchronized pull request $pr"))
            else -> result.add(format(json, "%repository% %sender% $action pull request $pr"))
        }
    }

    fun watch(result: MutableList<String>, json: JsonNode) {
        val action = if (json.text("action") == "started") "starred" else json.text("action")
        result.add(format(json, "%repository% %sender% $action us"))
    }

    fun team_add(result: MutableList<String>, json: JsonNode) {
        val team = "[**${json.text("team.name")}**](${json.text("sender.html_url")}/${json.text("team.name")})"
        if (!json.has("user")) {
            result.add(format(json, "%repository% %sender% added us to team $team"))
        } else {
            result.add(format(json, "%repository% %sender% added ${user(json["user"])} to team $team"))
        }
    }

    fun commit(json: JsonNode, commit: JsonNode): String {
        val branch = json.text("ref").replace("refs/heads/", "")
        val committer: String?
        if (commit.has("committer")) {
            committer = commit.textIf("committer.username")
        } else {
            committer = json.textIf("pusher_login")
        }
        val commitStr = "[**${commit.text("id").substring(0, 8)}**](${commit.text("url")})"
        val branchStr = "[**$branch**](${json.text("repository.url")}/tree/$branch)"
        return if (committer == null) {
            format(json, "%repository% *Unrecognized author* pushed commit $commitStr to $branchStr")
        } else {
            format(json, "%repository% [**$committer**](http://github.com/$committer) pushed commit $commitStr to $branchStr")
        }
    }

    fun push(result: MutableList<String>, json: JsonNode) {
        var distinctCommits = mutableListOf<JsonNode>()
        val nonDistinctCommits = mutableListOf<JsonNode>()

        for (obj in json["commits"]) {
            logger.info("commit: $obj")
            val distinct = obj["distinct"].asBoolean()
            val addTo = if (distinct) distinctCommits else nonDistinctCommits
            addTo.add(obj)
        }

        if (!nonDistinctCommits.isEmpty()) {
            result.add(pushEventSize(json, nonDistinctCommits.size))
        }

        stats.addCommits(json, distinctCommits)
        scope.launch {
            try {
                var additions = 0
                var deletions = 0
                val repository = json["repository"]
                distinctCommits.forEach {
                    val details = gitHubApi.commitDetails(repository, it)
                    additions += details?.additions ?: 0
                    deletions += details?.deletions ?: 0
                }
                stats.addAdditionsDeletions(repository, additions, deletions)
            } catch (e: Exception) {
                logger.error("Unable to get details for $result", e)
            }
        }

        if (distinctCommits.size > MAX_DISTINCT_COMMITS) {
            // if there's too many commits, not all should be informed about
            result.add(pushEventSize(json, distinctCommits.size) + " (only showing some of them below)")
            distinctCommits = distinctCommits.subList(distinctCommits.size - MAX_DISTINCT_COMMITS, distinctCommits.size)
        }

        distinctCommits.forEach {commitObj ->
            if (commitObj.text("message").indexOf("\n") > 0) {
                result.add(commit(json, commitObj))
                result.add(truncate(commitObj.text("message")))
            } else {
                result.add(truncate(commit(json, commitObj) + ": " + commitObj.text("message")))
            }
        }
    }

    fun pushEventSize(json: JsonNode, size: Int): String {
        val commitText = if (size == 1) "commit" else "commits"
        val branch = json.text("ref").replace("refs/heads/", "")
        return format(json, "%repository% [**${json.text("pusher.name")}**](https://github.com/${json.text("pusher.name")}) pushed $size $commitText to " +
                "[**$branch**](${json.text("repository.url")}/tree/$branch)")
    }

    fun postGithub(type: String, json: JsonNode): List<String> {
        val result = mutableListOf<String>()
        val callable = this::class.members.find { it.name == type }
        if (callable == null) {
            logger.info("No method found for $type")
            return emptyList()
        }
        callable.call(this, result, json)
        return result
    }

    fun repo(fullRepository: String): String {
        return "**\\[[$fullRepository](https://github.com/$fullRepository)]**"
    }

}
