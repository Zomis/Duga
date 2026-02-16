package net.zomis.duga

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.zomis.duga.chat.DugaClient
import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.chat.SqsPoster
import net.zomis.duga.features.DugaFeatures
import net.zomis.duga.features.StackExchange
import net.zomis.duga.server.ArgumentsCheck
import net.zomis.duga.server.webhooks.GitHubWebhook
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import net.zomis.duga.utils.github.text
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import net.zomis.duga.utils.stats.DugaStatsDynamoDB
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

class DugaLambda : RequestStreamHandler {

    init {
        System.setProperty("log4j.configurationFile", "log4j2_lambda.xml")
    }

    private val mapper = jacksonObjectMapper()
    private fun isApiGateway(node: JsonNode) = node.has("requestContext") && node["requestContext"].has("http")

    override fun handleRequest(
        input: InputStream,
        output: OutputStream,
        context: Context
    ) = runBlocking {
        val node = mapper.readTree(input)
        val poster = SqsPoster(System.getenv("SQS_QUEUE"))

        if (isApiGateway(node)) {
            return@runBlocking handleWebhook(node, output, context, poster, this)
        }

        val task = if (node.has("task")) node.get("task").textValue() else null
        println("Executing task $task")

        when (task) {
            "week-update" -> {
                StackExchange(poster).weeklyUpdate()
            }
            "unanswered" -> {
                val client = DugaClient()
                val stackExchangeApi = StackExchangeApi(client.client, System.getenv("STACK_EXCHANGE_API"))
                StackExchange(poster).codeReviewUnanswered(stackExchangeApi)
            }
            "star-race" -> {
                val client = DugaClient()
                val gitHubApi = GitHubApi(client.client, System.getenv("GITHUB_API"))
                val hookString = HookString(DugaStatsDynamoDB(), gitHubApi, this)
                StackExchange(poster).starRace(hookString, gitHubApi,
                    listOf("rubberduck-vba/Rubberduck", "decalage2/oletools")
                )
            }
            "daily-stats" -> {
                DugaFeatures(poster).dailyStats(DugaStatsDynamoDB(), clearStats = false)
            }
//            "webhook-github" -> {}
            else -> {
                println("Unknown Task: $task")
                println(input)
                println(context)
            }
        }
    }

    private suspend fun handleWebhook(
        rootEvent: JsonNode,
        output: OutputStream,
        context: Context,
        poster: DugaPoster,
        scope: CoroutineScope,
    ) {
        println(rootEvent)
        val node = mapper.readTree(readBody(rootEvent))
        println(node)
        // TODO: Handle GitHub webhook directly: Post to another SQS queue and then return 200 OK

        val client = DugaClient()
        val gitHubApi = GitHubApi(client.client, System.getenv("GITHUB_API"))
        val hookString = HookString(DugaStatsDynamoDB(), gitHubApi, scope)

        val room = rootEvent.get("pathParameters").get("room_name").asText()
        val gitHubEvent = rootEvent.get("headers").text("x-github-event")
        println("Incoming $gitHubEvent to room $room")
        GitHubWebhook(poster, hookString).lambdaPost(mapper, output, room, gitHubEvent, node)
    }

    fun readBody(event: JsonNode): String? {
        val bodyNode = event.get("body") ?: return null
        val raw = bodyNode.asText()

        val isBase64 = event.get("isBase64Encoded")?.asBoolean() == true

        return if (isBase64) {
            String(Base64.getDecoder().decode(raw))
        } else raw
    }

}