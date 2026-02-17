package net.zomis.duga

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.zomis.duga.chat.DugaClient
import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.chat.LoggingPoster
import net.zomis.duga.chat.SqsPoster
import net.zomis.duga.features.DugaFeatures
import net.zomis.duga.features.StackExchange
import net.zomis.duga.server.webhooks.GitHubWebhook
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import net.zomis.duga.utils.stackexchange.DynamoDbAnswerInvalidationCheckData
import net.zomis.duga.utils.stackexchange.ProgrammersClassification
import net.zomis.duga.utils.stackexchange.QuestionScanTask
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import net.zomis.duga.utils.stats.DugaStatsDynamoDB
import net.zomis.machlearn.text.TextClassification
import net.zomis.machlearn.text.TextFeatureMapper
import java.io.InputStream
import java.io.OutputStream

class DugaLambda : RequestStreamHandler {

    init {
        System.setProperty("log4j.configurationFile", "log4j2_lambda.xml")
    }

    private val mapper = jacksonObjectMapper()
    private fun isApiGateway(node: JsonNode) = node.has("requestContext") && node["requestContext"].has("http")
    private fun isSqsEvent(node: JsonNode): Boolean =
        node.has("Records") && node["Records"].isArray &&
            node["Records"].size() > 0 && node["Records"][0]["eventSource"]?.asText() == "aws:sqs"
    private val client by lazy { DugaClient() }
    private val stackExchangeApi by lazy {
        StackExchangeApi(client.client, System.getenv("STACK_EXCHANGE_API"))
    }
    private val commentsScanTaskPreTrained by lazy {
        val words = mapper.readValue<List<String>>(this::class.java.classLoader.getResourceAsStream("mapper-words.json")!!)
        val thetas = mapper.readValue<List<Double>>(this::class.java.classLoader.getResourceAsStream("mapper-thetas.json")!!)
        val textFeatureMapper = TextFeatureMapper(*words.toTypedArray())
        val classification = TextClassification(ProgrammersClassification::preprocessProgrammers, textFeatureMapper, thetas.toDoubleArray(), 0.4)
        DugaTasks(poster, stackExchangeApi).commentScanPretrained(classification)
    }
    private val poster = SqsPoster(System.getenv("SQS_QUEUE"))
//    private val poster = LoggingPoster()

    override fun handleRequest(
        input: InputStream,
        output: OutputStream,
        context: Context
    ) = runBlocking {
        val node = mapper.readTree(input)

        if (isSqsEvent(node)) {
            return@runBlocking handleSqs(node, output, context, poster, this)
        }

        val task = if (node.has("task")) node.get("task").textValue() else null
        println("Executing task $task")

        when (task) {
            "week-update" -> {
                StackExchange(poster).weeklyUpdate()
            }
            "unanswered" -> {
                StackExchange(poster).codeReviewUnanswered(stackExchangeApi)
            }
            "star-race" -> {
                val gitHubApi = GitHubApi(client.client, System.getenv("GITHUB_API"))
                val hookString = HookString(DugaStatsDynamoDB(), gitHubApi, this)
                StackExchange(poster).starRace(hookString, gitHubApi,
                    listOf("rubberduck-vba/Rubberduck", "decalage2/oletools")
                )
            }
            "comment-scan" -> runCommentScan(this)
            "answer-invalidation" -> runAnswerInvalidationCheck()
            "daily-stats" -> {
                DugaFeatures(poster).dailyStats(DugaStatsDynamoDB(), clearStats = false)
            }
            else -> {
                println("Unknown Task: $task")
                println(input)
                println(context)
            }
        }
    }

    suspend fun runAnswerInvalidationCheck() {
        QuestionScanTask(poster, stackExchangeApi, "codereview",
            DynamoDbAnswerInvalidationCheckData()
        ).run()
    }

    suspend fun runCommentScan(scope: CoroutineScope) {
        commentsScanTaskPreTrained.run(scope)
    }

    private suspend fun handleSqs(
        rootEvent: JsonNode,
        output: OutputStream,
        context: Context,
        poster: DugaPoster,
        scope: CoroutineScope,
    ) {
        val messages = rootEvent.get("Records")
        for (message in messages) {
            val body = message["body"].asText()
            val room = message["messageAttributes"].get("room").get("stringValue").asText()
            val gitHubEvent = message["messageAttributes"].get("event-type").get("stringValue").asText()
            val node = mapper.readTree(body)

            println(node)
            val client = DugaClient()
            val gitHubApi = GitHubApi(client.client, System.getenv("GITHUB_API"))
            val hookString = HookString(DugaStatsDynamoDB(), gitHubApi, scope)

            println("Incoming $gitHubEvent to room $room")
            GitHubWebhook(poster, hookString).lambdaPost(mapper, output, room, gitHubEvent, node)
        }
    }

}