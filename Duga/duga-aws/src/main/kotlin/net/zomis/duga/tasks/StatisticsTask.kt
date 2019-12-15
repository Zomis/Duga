package net.zomis.duga.tasks

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.*
import net.zomis.duga.aws.DugaMessage
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec

class StatisticsTask(private val rooms: String) : DugaTask {

    private val hashKeyName = "Identifier"
    private val secretKeyName = "SecretKey"
    private val secretKeyIndex = secretKeyName + "Index"
    private val tableName = "Duga-Stats"
    private val dynamoDB = AmazonDynamoDBClientBuilder.standard()
        .withRegion(Regions.EU_CENTRAL_1)
        .build()
    private val ddb = DynamoDB(dynamoDB)
    private val table = ddb.getTable(tableName)

    override fun perform(): List<DugaMessage> {
        val messages = statsMessages()
        return rooms.split(",").flatMap { room ->
            messages.map { message ->
                DugaMessage(room, message)
            }
        }
    }

    fun statistic(application: String, secretKey: String, values: Map<String, Int>): Boolean {
        val updateExpression = "ADD " + values.entries.joinToString(", ") {
            "${it.key} :${it.key}"
        }
        val valueMap = values.entries.fold(ValueMap()) { r, entry ->
            r.withNumber(":" + entry.key, entry.value)
        }
        println(updateExpression)
        println(valueMap)

        val hashKeyQuery = QuerySpec().withHashKey(secretKeyName, secretKey)
        val hashKeyResult = table.getIndex(secretKeyIndex).query(hashKeyQuery)
        val identifier = hashKeyResult.firstOrNull()?.get(hashKeyName) as String?
        if (identifier != application) {
            println("Database identifier '$identifier' for secret '$secretKey' did not match '$application'")
            return false
        }

        val spec = UpdateItemSpec().withPrimaryKey(hashKeyName, identifier)
            .withUpdateExpression(updateExpression)
            .withValueMap(valueMap)
        table.updateItem(spec)
        return true
    }

    fun repository(name: String, values: Map<String, Int>) {
        val set = "SET $secretKeyName=:secretKey"
        val updateExpression = "$set ADD " + values.entries.joinToString(", ") {
            "${it.key} :${it.key}"
        }
        val valueMap = values.entries.fold(ValueMap()) { r, entry ->
            r.withNumber(":" + entry.key, entry.value)
        }.withString(":secretKey", "this-is-my-secret")
        println(updateExpression)
        println(valueMap)
        val spec = UpdateItemSpec().withPrimaryKey(hashKeyName, name)
            .withUpdateExpression(updateExpression)
            .withValueMap(valueMap)
            .withReturnValues(ReturnValue.UPDATED_NEW)
        val result = table.updateItem(spec)
        println(result)
        println(result.updateItemResult)
    }

    private fun statsMessages(): List<String> {
        return listOf()
    }

    private fun queryAndClear() {
        dynamoDB.scan(ScanRequest(tableName))
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
    val task = StatisticsTask("20298")
//    task.createTables(listOf(task.createTable()))

    task.statistic("testing", "super-secret", mapOf("AWS_Lambdas_Deployed" to 2))
    if (true) return
    task.repository("just/testing", mapOf(
        "commits" to 5,
        "opened_issues" to 1,
        "closed_issues" to 2,
        "issue_comments" to 7,
        "additions" to 209,
        "deletions" to 59
    ))
}