package net.zomis.duga.utils.stats

import com.fasterxml.jackson.databind.JsonNode
import net.zomis.duga.utils.github.text

data class DugaStat(val group: String, val url: String) {
    private val values = mutableMapOf<String, Int>()

    fun reset(): Map<String, Int> {
        synchronized(values) {
            val current = values.toMap()
            values.clear()
            return current
        }
    }
    fun add(category: String, value: Int) {
        synchronized(values) {
            values.merge(category, value) { a, b -> a + b }
        }
    }
}
interface DugaStats {
    fun add(group: String, url: String, category: String, value: Int) {}
    fun addIssue(issue: JsonNode, change: Int)
    fun addIssueComment(comment: JsonNode)
    fun addCommits(json: JsonNode, commits: List<JsonNode>)
    fun allStats(): List<DugaStat>
    fun addAdditionsDeletions(repository: JsonNode?, additions: Int, deletions: Int) {
        if (repository == null) return
        this.add(repository.text("full_name"), repository.text("html_url"), "additions", additions)
        this.add(repository.text("full_name"), repository.text("html_url"), "deletions", deletions)
    }
}
class DugaStatsNoOp: DugaStats {

    override fun add(group: String, url: String, category: String, value: Int) {}

    override fun addIssue(issue: JsonNode, change: Int) {}

    override fun addIssueComment(comment: JsonNode) {}

    override fun addCommits(json: JsonNode, commits: List<JsonNode>) {}

    override fun allStats(): List<DugaStat> = emptyList()
}

class DugaStatsInternalMap: DugaStats {
    private val stats: MutableMap<String, DugaStat> = mutableMapOf()
    private val lock = Any()

    private fun stat(group: String, url: String): DugaStat {
        synchronized(lock) {
            return stats.computeIfAbsent(group) { DugaStat(group, url) }
        }
    }

    override fun add(group: String, url: String, category: String, value: Int) {
        val stat = stat(group, url)
        stat.add(category, value)
    }

    override fun addIssue(issue: JsonNode, change: Int) {
        if (change > 0) {
            this.add(issue.text("repository.full_name"), issue.text("repository.html_url"), "issues opened", 1)
        } else {
            this.add(issue.text("repository.full_name"), issue.text("repository.html_url"), "issues closed", 1)
        }
    }

    override fun addIssueComment(comment: JsonNode) {
        this.add(comment.text("repository.full_name"), comment.text("repository.html_url"), "issue comments", 1)
    }

    override fun addCommits(json: JsonNode, commits: List<JsonNode>) {
        this.add(json.text("repository.full_name"), json.text("repository.html_url"), "commits", 1)
    }

    override fun allStats(): List<DugaStat> {
        synchronized(lock) {
            val current = stats.values.toList()
            stats.clear()
            return current
        }
    }

}