package net.zomis.duga.aws

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnection
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import javax.jms.Session
import javax.jms.TextMessage
import javax.jms.Message

data class DugaMessage(val room: String, val message: String)

class FetchMessage {
    val queueName = "Duga-Messages.fifo"

    fun connect(): SQSConnection {
        val connectionFactory = SQSConnectionFactory(
            ProviderConfiguration(),
            AmazonSQSClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(DefaultAWSCredentialsProviderChain())
        )

        val connection = connectionFactory.createConnection()
        return connection
    }

    fun fetch(handler: (DugaMessage) -> Unit) {
        val connection = connect()
        val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        println("Creating queue '$queueName'...")
        val queue = session.createQueue(queueName)
        val consumer = session.createConsumer(queue)

        // Start receiving incoming messages
        connection.start()

        var receivedMessage: Message?
        do {
            receivedMessage = consumer.receive(1000)
            val converted = convertMessage(receivedMessage)
            if (converted != null) {
                handler(converted)
            }
        } while (receivedMessage != null)
        // Receive a message from 'MyQueue' and wait up to 1 second

        connection.close()
    }

    private fun convertMessage(receivedMessage: Message?): DugaMessage? {
        if (receivedMessage == null) {
            return null
        }
        val textMessage = receivedMessage as TextMessage

        // Cast the received message as TextMessage and display the text
        println("Received: " + textMessage.text)
        val room = textMessage.getStringProperty("JMSXGroupID")
        System.out.println("Group id: $room")
        System.out.println("Message deduplication id: " + textMessage.getStringProperty("JMS_SQS_DeduplicationId"))
        System.out.println("Message sequence number: " + textMessage.getStringProperty("JMS_SQS_SequenceNumber"))
        return DugaMessage(room, textMessage.text)
    }

    fun send() {
        val connection = connect()
        val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)

        // Create a queue identity and specify the queue name to the session
        val queue = session.createQueue(queueName)

        // Create a producer for the 'MyQueue'
        val producer = session.createProducer(queue)

        // Create the text message
        val message = session.createTextMessage("Hello from AWS!")
        message.setStringProperty("JMSXGroupID", "16134")

        // Send the message
        producer.send(message)
        println("JMS Message " + message.getJMSMessageID())
    }

}

fun main(args: Array<String>) {
    FetchMessage().send()
}