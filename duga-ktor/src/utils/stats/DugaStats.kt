package net.zomis.duga.utils.stats

import com.fasterxml.jackson.databind.JsonNode

interface DugaStats {
    fun addIssue(issue: JsonNode, change: Int)
    fun addIssueComment(comment: JsonNode)
    fun addCommits(json: JsonNode, commits: List<JsonNode>)
}
class DugaStatsNoOp: DugaStats {
    override fun addIssue(issue: JsonNode, change: Int) {}

    override fun addIssueComment(comment: JsonNode) {}

    override fun addCommits(json: JsonNode, commits: List<JsonNode>) {}
}
