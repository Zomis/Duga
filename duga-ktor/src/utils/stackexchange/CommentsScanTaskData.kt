package net.zomis.duga.utils.stackexchange

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.getItem
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.putItem
import java.time.Instant

interface CommentsScanTaskData {
    var nextFetch: Instant
    var lastComment: Long
    var fromDate: Long
    var remainingQuota: Long
    suspend fun save()
    suspend fun load()
}

class InMemoryCommentsScanTaskData : CommentsScanTaskData {
    override var nextFetch = Instant.now()
    override var lastComment: Long = 0
    override var fromDate: Long = 0
    override var remainingQuota: Long = 0

    override suspend fun load() {}
    override suspend fun save() {}
}

class DynamoDbCommentsScanTaskData(
    val current: InMemoryCommentsScanTaskData = InMemoryCommentsScanTaskData()
) : CommentsScanTaskData by current {
    private val dynamoDb get() = DynamoDbClient {
        this.region = "eu-central-1"
    }

    override suspend fun load() {
        dynamoDb.use { db ->
            val getItem = db.getItem {
                this.tableName = "duga"
                this.key = mapOf("PK" to AttributeValue.S("task"), "SK" to AttributeValue.S("comment-scan"))
            }
            val item = getItem.item
            if (item != null) {
                item["lastComment"]?.asN()?.toLong()?.also { current.lastComment = it }
                item["fromDate"]?.asN()?.toLong()?.also { current.fromDate = it }
                item["remainingQuota"]?.asN()?.toLong()?.also { current.remainingQuota = it }
                item["nextFetch"]?.asN()?.toLong()?.also { current.nextFetch = Instant.ofEpochMilli(it) }
            }
        }
    }

    override suspend fun save() {
        dynamoDb.use { db ->
            db.putItem {
                tableName = "duga"
                item = mapOf(
                    "PK" to AttributeValue.S("task"),
                    "SK" to AttributeValue.S("comment-scan"),
                    "lastComment" to AttributeValue.N(current.lastComment.toString()),
                    "fromDate" to AttributeValue.N(current.fromDate.toString()),
                    "remainingQuota" to AttributeValue.N(current.remainingQuota.toString()),
                    "nextFetch" to AttributeValue.N(current.nextFetch.toEpochMilli().toString()),
                )
            }
        }
    }
}