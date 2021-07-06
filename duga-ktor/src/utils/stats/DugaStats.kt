package net.zomis.duga.utils.stats

import com.fasterxml.jackson.databind.JsonNode
import net.zomis.duga.utils.github.text
import org.slf4j.LoggerFactory
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest

data class DugaStat(val key: String, val displayName: String, val url: String) {
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
    fun addKey(key: String, displayName: String, url: String, category: String, value: Int)
    fun add(group: String, url: String, category: String, value: Int) {
        this.addKey("github/$group", group, url, category, value)
    }
    fun addIssue(issue: JsonNode, change: Int) {
        if (change > 0) {
            this.add(issue.text("repository.full_name"), issue.text("repository.html_url"), "issues opened", 1)
        } else {
            this.add(issue.text("repository.full_name"), issue.text("repository.html_url"), "issues closed", 1)
        }
    }
    fun addIssueComment(comment: JsonNode) {
        this.add(comment.text("repository.full_name"), comment.text("repository.html_url"), "issue comments", 1)
    }
    fun addCommits(json: JsonNode, commits: List<JsonNode>) {
        this.add(json.text("repository.full_name"), json.text("repository.html_url"), "commits", commits.size)
    }
    fun allStats(): List<DugaStat>
    fun addAdditionsDeletions(repository: JsonNode?, additions: Int, deletions: Int) {
        if (repository == null) return
        this.add(repository.text("full_name"), repository.text("html_url"), "additions", additions)
        this.add(repository.text("full_name"), repository.text("html_url"), "deletions", deletions)
    }
}
class DugaStatsNoOp: DugaStats {
    override fun addKey(key: String, displayName: String, url: String, category: String, value: Int) {}
    override fun allStats(): List<DugaStat> = emptyList()
}

class DugaStatsInternalMap: DugaStats {
    private val stats: MutableMap<String, DugaStat> = mutableMapOf()
    private val lock = Any()

    private fun stat(key: String, displayName: String, url: String): DugaStat {
        synchronized(lock) {
            return stats.computeIfAbsent(key) { DugaStat(key, displayName, url) }
        }
    }

    override fun addKey(key: String, displayName: String, url: String, category: String, value: Int) {
        val stat = stat(key, displayName, url)
        stat.add(category, value)
    }

    override fun allStats(): List<DugaStat> {
        synchronized(lock) {
            val current = stats.values.toList()
            stats.clear()
            return current
        }
    }

}

class DugaStatsDynamoDB: DugaStats {
    val tableName = "Duga-Stats"
    val fieldKey = "Key"
    val fieldDisplayName = "DisplayName"
    val fieldUrl = "Url"

    private val logger = LoggerFactory.getLogger(DugaStatsDynamoDB::class.java)

    private val dynamoDb = DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1)
            .httpClient(ApacheHttpClient.builder().build())
            .build()

    override fun addKey(key: String, displayName: String, url: String, category: String, value: Int) {
        val itemValues = mapOf(
            fieldKey to AttributeValue.builder().s(key).build(),
            fieldDisplayName to AttributeValue.builder().s(displayName).build()
        )
        val item = dynamoDb.getItem { it.tableName(tableName).key(itemValues) }
        logger.info("addKey returned {}", item)

        if (item.hasItem()) {
            dynamoDb.putItem(PutItemRequest.builder().tableName(tableName).item(
                item.item().toMutableMap().also {
                    val newValue = (it[category]?.n()?.toInt() ?: 0) + value
                    it[category] = AttributeValue.builder().n(newValue.toString()).build()
                }
            ).build())
        } else {
            dynamoDb.putItem(PutItemRequest.builder().tableName(tableName).item(itemValues + mapOf(
                fieldUrl to AttributeValue.builder().s(url).build(),
                category to AttributeValue.builder().n(value.toString()).build()
            )).build())
        }
    }

    override fun allStats(): List<DugaStat> {
        val scanResponse = dynamoDb.scan(ScanRequest.builder().tableName(tableName).build())
        val results = scanResponse.items().map { item ->
            logger.info("scanResponse returned {}", item)
            val stat = DugaStat(item.getValue(fieldKey).s(), item.getValue(fieldDisplayName).s(), item.getValue(fieldUrl).s())
            item.filter { e -> e.key !in setOf(fieldKey, fieldDisplayName, fieldUrl) }.forEach { e ->
                stat.add(e.key, e.value.n().toInt())
            }
            stat
        }
        val remove = scanResponse.items().map {
            DeleteItemRequest.builder().tableName(tableName).key(it.filter { e -> e.key in setOf(fieldKey, fieldDisplayName) }).build()
        }
        remove.forEach {
            dynamoDb.deleteItem(it)
        }
        return results
    }
}
