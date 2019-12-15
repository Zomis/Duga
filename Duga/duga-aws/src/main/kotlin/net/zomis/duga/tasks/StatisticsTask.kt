package net.zomis.duga.tasks

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.*
import net.zomis.duga.aws.DugaMessage
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import java.util.UUID

class StatisticsTask(private val rooms: String, private val reset: Boolean) : DugaTask {

    val sortReplace = mapOf("commits" to "0", "additions" to "1", "deletions" to "2", "issue_comments" to "zzzz")

    private val hashKeyName = "Identifier"
    private val secretKeyName = "SecretKey"
    private val secretKeyIndex = secretKeyName + "Index"
    private val tableName = "Duga-Stats"
    private val itemURL = "displayUrl"
    private val nonStatsItems = listOf(hashKeyName, secretKeyName, itemURL)

    private val dynamoDB = AmazonDynamoDBClientBuilder.standard()
        .withRegion(Regions.EU_CENTRAL_1)
        .build()
    private val ddb = DynamoDB(dynamoDB)
    private val table = ddb.getTable(tableName)

    override fun perform(): List<DugaMessage> {
        val messages = statsMessages(reset)
        return rooms.split(",").flatMap { room ->
            messages.map { message ->
                DugaMessage(room, message)
            }
        }
    }

    fun statistic(application: String, secretKey: String, values: Map<String, Int>): Boolean {
        val hashKeyQuery = QuerySpec().withHashKey(secretKeyName, secretKey)
        val hashKeyResult = table.getIndex(secretKeyIndex).query(hashKeyQuery)
        val identifier = hashKeyResult.firstOrNull()?.get(hashKeyName) as String?
        if (identifier != application) {
            println("Database identifier '$identifier' for secret '$secretKey' did not match '$application'")
            return false
        }

        val spec = update(identifier, values, null)
        table.updateItem(spec)
        return true
    }

    fun repository(name: String, url: String, values: Map<String, Int>) {
        table.updateItem(update(name, values, url))
    }

    private fun update(primaryKey: String, values: Map<String, Int>, repositoryUrl: String?): UpdateItemSpec {
        val set = if (repositoryUrl != null) "SET $secretKeyName=:secretKey, $itemURL=:url" else ""

        val updateExpression = "$set ADD " + values.entries.joinToString(", ") {
            "${it.key} :${it.key}"
        }
        var valueMap = values.entries.fold(ValueMap()) { r, entry ->
            r.withNumber(":" + entry.key, entry.value)
        }
        if (repositoryUrl != null) {
            valueMap = valueMap.withString(":secretKey", UUID.randomUUID().toString())
                .withString(":url", repositoryUrl)
        }

        return UpdateItemSpec().withPrimaryKey(hashKeyName, primaryKey)
            .withUpdateExpression(updateExpression)
            .withValueMap(valueMap)
    }

    private fun resetItem(item: Map<String, AttributeValue>) {
        if (item.containsKey("commits")) {
            table.deleteItem(hashKeyName, item[hashKeyName]!!.s)
        } else {
            val values = item.minus(nonStatsItems)
            val updateExpression = "SET " + values.entries.joinToString(", ") {
                "${it.key} :${it.key}"
            }
            val valueMap = values.entries.fold(ValueMap()) { r, entry ->
                r.withNumber(":" + entry.key, 0)
            }
            val update = UpdateItemSpec().withPrimaryKey(hashKeyName, item[hashKeyName]!!.s)
                .withUpdateExpression(updateExpression)
                .withValueMap(valueMap)
            table.updateItem(update)
        }
    }

    private fun statsMessages(reset: Boolean): List<String> {
        val scan = dynamoDB.scan(ScanRequest(tableName))
        if (reset) {
            // Remove all repositories
            // Reset statistics on all secrets
            scan.items.map(this::resetItem)
        }
        return listOf("***RELOAD!***").plus(scan.items.map(this::itemToMessage))
    }

    private fun prettyPrintKey(key: String): String {
        return key.replace('_', ' ')
    }

    private fun itemToMessage(item: Map<String, AttributeValue>): String {
        val displayName = item[hashKeyName]!!.s
        val url = item[itemURL]?.s
        val prefix = if (url == null) "**[$displayName]**" else "**\\[[$displayName]($url)]**"

        return "$prefix " + item.minus(nonStatsItems).entries
            .sortedBy { sortReplace[it.key] ?: it.key }
            .joinToString(". ") {
            "${it.value.n} ${prettyPrintKey(it.key)}"
        }
    }

    fun createTables(requests: List<CreateTableRequest>) {
        val exists = dynamoDB.listTables()
        println(exists.tableNames)
        val existingTables = exists.tableNames.toSet()

        requests.filter { !existingTables.contains(it.tableName) }.forEach {
            dynamoDB.createTable(it)
        }
    }

    fun createTable(): CreateTableRequest {

        val attributeDefinitions = listOf(
            AttributeDefinition(hashKeyName, ScalarAttributeType.S),
            AttributeDefinition(secretKeyName, ScalarAttributeType.S)
        )

        val ks = ArrayList<KeySchemaElement>()
        ks.add(KeySchemaElement(hashKeyName, KeyType.HASH))

        val secondaryIndex = GlobalSecondaryIndex()
            .withIndexName(secretKeyName + "Index")
            .withProjection(Projection().withProjectionType(ProjectionType.KEYS_ONLY))
            .withProvisionedThroughput(ProvisionedThroughput(2L, 2L))
        secondaryIndex.setKeySchema(listOf(KeySchemaElement(secretKeyName, KeyType.HASH)))

        val provisionedThroughput = ProvisionedThroughput(2L, 2L)

        return CreateTableRequest()
            .withTableName(tableName)
            .withAttributeDefinitions(attributeDefinitions)
            .withKeySchema(ks)
            .withBillingMode(BillingMode.PROVISIONED)
            .withGlobalSecondaryIndexes(secondaryIndex)
            .withProvisionedThroughput(provisionedThroughput)
    }

}

fun main(args: Array<String>) {
    val task = StatisticsTask("20298", false)
//    task.createTables(listOf(task.createTable()))

    task.statistic("testing", "super-secret", mapOf("AWS_Tests_Succeeded" to 1))
    val result = task.perform()
    println(result)
    if (true) return
    task.repository("just/testing", "https://github.com/Zomis/test", mapOf(
        "commits" to 1,
        "opened_issues" to 1,
        "closed_issues" to 2,
        "issue_comments" to 7,
        "additions" to 209,
        "deletions" to 59
    ))
}