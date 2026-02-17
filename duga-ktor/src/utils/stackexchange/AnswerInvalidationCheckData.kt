package net.zomis.duga.utils.stackexchange

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.getItem
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.putItem
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

interface AnswerInvalidationCheckData {
    var lastCheck: Instant
    suspend fun load() {}
    suspend fun save() {}
}

class InMemoryAnswerInvalidationCheckData : AnswerInvalidationCheckData {
    override var lastCheck: Instant = Instant.now().minus(1, ChronoUnit.MINUTES)

    override suspend fun load() {}
    override suspend fun save() {}
}

class DynamoDbAnswerInvalidationCheckData(
    val current: InMemoryAnswerInvalidationCheckData = InMemoryAnswerInvalidationCheckData()
) : AnswerInvalidationCheckData by current {
    private val dynamoDb get() = DynamoDbClient {
        this.region = "eu-central-1"
    }

    override suspend fun load() {
        dynamoDb.use { db ->
            val getItem = db.getItem {
                this.tableName = "duga"
                this.key = mapOf("PK" to AttributeValue.S("task"), "SK" to AttributeValue.S("answer-invalidation"))
            }
            val item = getItem.item
            if (item != null) {
                item["lastCheck"]?.asN()?.toLong()?.also { current.lastCheck = Instant.ofEpochMilli(it) }
            }
        }
    }

    override suspend fun save() {
        dynamoDb.use { db ->
            db.putItem {
                tableName = "duga"
                item = mapOf(
                    "PK" to AttributeValue.S("task"),
                    "SK" to AttributeValue.S("answer-invalidation"),
                    "lastCheck" to AttributeValue.N(current.lastCheck.toEpochMilli().toString()),
                )
            }
        }
    }
}