package net.zomis.duga.utils.stats

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.getItem
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import aws.sdk.kotlin.services.dynamodb.putItem
import aws.sdk.kotlin.services.dynamodb.query
import aws.sdk.kotlin.services.dynamodb.scan
import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.runBlocking
import net.zomis.duga.server.webhooks.StatsWebhook
import net.zomis.duga.utils.github.text
import org.slf4j.LoggerFactory
import software.amazon.awssdk.regions.Region

data class DugaStat(val key: String, val displayName: String, val url: String) {
    private val values = mutableMapOf<String, Int>()

    fun current(): Map<String, Int> {
        synchronized(values) {
            return values.toMap()
        }
    }

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
    suspend fun addKey(key: String, displayName: String, url: String, category: String, value: Int)
    suspend fun add(group: String, url: String, category: String, value: Int) {
        this.addKey("github/$group", group, url, category, value)
    }
    fun addIssue(issue: JsonNode, change: Int) = runBlocking {
        if (change > 0) {
            add(issue.text("repository.full_name"), issue.text("repository.html_url"), "issues opened", 1)
        } else {
            add(issue.text("repository.full_name"), issue.text("repository.html_url"), "issues closed", 1)
        }
    }
    fun addIssueComment(comment: JsonNode) = runBlocking {
        add(comment.text("repository.full_name"), comment.text("repository.html_url"), "issue comments", 1)
    }
    fun addCommits(json: JsonNode, commits: List<JsonNode>) = runBlocking {
        add(json.text("repository.full_name"), json.text("repository.html_url"), "commits", commits.size)
    }
    suspend fun currentStats(): List<DugaStat>
    suspend fun clearStats(): List<DugaStat>
    suspend fun addAdditionsDeletions(repository: JsonNode?, additions: Int, deletions: Int) {
        if (repository == null) return
        this.add(repository.text("full_name"), repository.text("html_url"), "additions", additions)
        this.add(repository.text("full_name"), repository.text("html_url"), "deletions", deletions)
    }
}
class DugaStatsNoOp: DugaStats {
    override suspend fun addKey(key: String, displayName: String, url: String, category: String, value: Int) {}
    override suspend fun currentStats(): List<DugaStat> = emptyList()
    override suspend fun clearStats(): List<DugaStat> = emptyList()
}

class DugaStatsInternalMap: DugaStats {
    private val stats: MutableMap<String, DugaStat> = mutableMapOf()
    private val lock = Any()

    private fun stat(key: String, displayName: String, url: String): DugaStat {
        synchronized(lock) {
            return stats.computeIfAbsent(key) { DugaStat(key, displayName, url) }
        }
    }

    override suspend fun addKey(key: String, displayName: String, url: String, category: String, value: Int) {
        val stat = stat(key, displayName, url)
        stat.add(category, value)
    }

    override suspend fun clearStats(): List<DugaStat> {
        synchronized(lock) {
            val current = stats.values.toList()
            stats.clear()
            return current
        }
    }

    override suspend fun currentStats(): List<DugaStat> {
        synchronized(lock) {
            return stats.values.toList()
        }
    }

}

class DugaStatsDynamoDB : DugaStats {
    val tableName = "Duga-Stats"
    val fieldKey = "Key"
    val fieldDisplayName = "DisplayName"
    val fieldUrl = "Url"

    private val logger = LoggerFactory.getLogger(DugaStatsDynamoDB::class.java)

    private val dynamoDb = DynamoDbClient { region = Region.EU_CENTRAL_1.id() }

    override suspend fun addKey(key: String, displayName: String, url: String, category: String, value: Int) {
        val itemValues = mapOf(
            fieldKey to AttributeValue.S(key),
            fieldDisplayName to AttributeValue.S(displayName)
        )
        val item = dynamoDb.getItem {
            this.tableName = this@DugaStatsDynamoDB.tableName
            this.key = itemValues
        }
        logger.info("addKey returned {}", item)

        if (item.item != null) {
            dynamoDb.putItem {
                this.tableName = this@DugaStatsDynamoDB.tableName
                this.item = item.item!!.toMutableMap().also {
                    val newValue = (it[category]?.asN()?.toInt() ?: 0) + value
                    it[category] = AttributeValue.N(newValue.toString())
                }
            }
        } else {
            dynamoDb.putItem {
                this.tableName = this@DugaStatsDynamoDB.tableName
                this.item = itemValues + mapOf(
                    fieldUrl to AttributeValue.S(url),
                    category to AttributeValue.N(value.toString())
                )
            }
        }
    }

    override suspend fun currentStats(): List<DugaStat> = allStats(clear = false)
    override suspend fun clearStats(): List<DugaStat> = allStats(clear = true)

    private suspend fun allStats(clear: Boolean): List<DugaStat> {
        val scanResponse = dynamoDb.scan {
            this.tableName = this@DugaStatsDynamoDB.tableName
        }
        val items = scanResponse.items ?: return emptyList()
        val results = items.map { item ->
            logger.info("scanResponse returned {}", item)
            val stat = DugaStat(item.getValue(fieldKey).asS(), item.getValue(fieldDisplayName).asS(), item.getValue(fieldUrl).asS())
            item.filter { e -> e.key !in setOf(fieldKey, fieldDisplayName, fieldUrl) }.forEach { e ->
                stat.add(e.key, e.value.asN().toInt())
            }
            stat
        }
        if (!clear) {
            return results
        }

        val remove = items.map {
            DeleteItemRequest {
                this.tableName = this@DugaStatsDynamoDB.tableName
                key = it.filter { e -> e.key in setOf(fieldKey, fieldDisplayName) }
            }
        }
        remove.forEach {
            dynamoDb.deleteItem(it)
        }
        return results
    }
}

class DugaStatsNewDynamoDB : DugaStats {
    val tableName = "duga"
    val PK = "PK"
    val SK = "SK"
    val fieldKey = SK
    val fieldDisplayName = "displayName"
    val fieldUrl = "url"

    private val logger = LoggerFactory.getLogger(DugaStatsDynamoDB::class.java)

    private val dynamoDb = DynamoDbClient { region = Region.EU_CENTRAL_1.id() }

    override suspend fun addKey(key: String, displayName: String, url: String, category: String, value: Int) {
        val itemValues = mapOf(
            PK to AttributeValue.S("stats"),
            SK to AttributeValue.S(key),
//            fieldDisplayName to AttributeValue.S(displayName)
        )
        val item = dynamoDb.getItem {
            this.tableName = this@DugaStatsNewDynamoDB.tableName
            this.key = itemValues
        }
        logger.info("addKey returned {}", item)

        if (item.item != null) {
            dynamoDb.putItem {
                this.tableName = this@DugaStatsNewDynamoDB.tableName
                this.item = item.item!!.toMutableMap().also {
                    val newValue = (it[category]?.asN()?.toInt() ?: 0) + value
                    it[category] = AttributeValue.N(newValue.toString())
                }
            }
        } else {
            dynamoDb.putItem {
                this.tableName = this@DugaStatsNewDynamoDB.tableName
                this.item = itemValues + mapOf(
                    fieldUrl to AttributeValue.S(url),
                    fieldDisplayName to AttributeValue.S(displayName),
                    category to AttributeValue.N(value.toString())
                )
            }
        }
    }

    override suspend fun currentStats(): List<DugaStat> = allStats(clear = false)
    override suspend fun clearStats(): List<DugaStat> = allStats(clear = true)

    private suspend fun allStats(clear: Boolean): List<DugaStat> {
        val scanResponse = dynamoDb.query {
            this.tableName = this@DugaStatsNewDynamoDB.tableName
            keyConditionExpression = "PK = :pk"
            expressionAttributeValues = mapOf(":pk" to AttributeValue.S("stats"))
        }
        val items = scanResponse.items ?: return emptyList()
        val results = items.map { item ->
            logger.info("scanResponse returned {}", item)
            val stat = DugaStat(item.getValue(fieldKey).asS(), item.getValue(fieldDisplayName).asS(), item.getValue(fieldUrl).asS())
            item.filter { e -> e.key !in setOf(PK, fieldKey, fieldDisplayName, fieldUrl) }.forEach { e ->
                stat.add(e.key, e.value.asN().toInt())
            }
            stat
        }
        if (!clear) {
            return results
        }

        val remove = items.map {
            DeleteItemRequest {
                this.tableName = this@DugaStatsNewDynamoDB.tableName
                key = it.filter { e -> e.key in setOf(fieldKey, fieldDisplayName) }
            }
        }
        remove.forEach {
            dynamoDb.deleteItem(it)
        }
        return results
    }

    suspend fun fetchConfig(authToken: String, application: String): StatsWebhook.Config.ItemConfig? {
        val item = dynamoDb.getItem {
            this.tableName = this@DugaStatsNewDynamoDB.tableName
            this.key = mapOf(
                PK to AttributeValue.S("stats-config"),
                SK to AttributeValue.S(authToken),
            )
        }.item
        if (item == null) return null
        val foundDisplayName = item["displayName"]?.asS()
        if (foundDisplayName != application) {
            logger.warn("Mismatching stats config: Found $foundDisplayName but expected $application")
            return null
        }

        return StatsWebhook.Config.ItemConfig(
            authToken, application, item["url"]?.asS() ?: ""
        )
    }
}
