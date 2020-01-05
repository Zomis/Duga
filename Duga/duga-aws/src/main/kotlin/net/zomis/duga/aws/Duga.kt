package net.zomis.duga.aws

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import javax.jms.Session
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry
import com.amazonaws.services.sqs.model.SendMessageBatchRequest

class Duga {

    private val queueUrl = "https://sqs.eu-central-1.amazonaws.com/343175303479/Duga-Messages.fifo"

    fun sendMany(room: String, messages: List<String>) {
        return sendMany(messages.map { DugaMessage(room, it) })
    }

    fun sendMany(messages: List<DugaMessage>) {
        val chunks = messages.chunked(10)
        chunks.forEach {chunk ->
            val batch = SendMessageBatchRequest()
                .withQueueUrl(queueUrl)
                .withEntries(
                    chunk.mapIndexed { index, dugaMessage ->
                        SendMessageBatchRequestEntry(index.toString(), dugaMessage.message)
                            .withMessageGroupId(dugaMessage.room)
                            .withMessageDeduplicationId(dugaMessage.md5())
                    }
                )

            val sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(DefaultAWSCredentialsProviderChain()).build()
            val result = sqs.sendMessageBatch(batch)
            println(result)
        }
    }

    fun send(message: DugaMessage) {
        val connection = DugaSQS().connect()
        val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

        // Create a queue identity and specify the queue name to the session
        val queue = session.createQueue(DugaSQS().queueName)

        // Create a producer for the 'MyQueue'
        val producer = session.createProducer(queue)

        // Create the text message
        val jmsMessage = session.createTextMessage(message.message)
        jmsMessage.setStringProperty("JMSXGroupID", message.room)
        jmsMessage.setStringProperty("JMS_SQS_DeduplicationId", message.md5())

        // Send the message
        producer.send(jmsMessage)
        println("JMS Message " + jmsMessage.jmsMessageID)
        connection.close()
    }

}

fun main(args: Array<String>) {
    Duga().sendMany("16134", listOf("Hello @skiwi", "Hello @Simon", "Hello @Phrancis"))
}
