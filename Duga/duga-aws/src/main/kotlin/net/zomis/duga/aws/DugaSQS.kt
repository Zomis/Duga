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
import java.security.MessageDigest

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
data class DugaMessage(val room: String, val message: String) {
    fun md5(): String {
        val bytesOfMessage = "$room$message".toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("MD5")
        return md.digest(bytesOfMessage).toHexString()
    }
}

class DugaSQS {
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
        val queue = session.createQueue(queueName)
        val consumer = session.createConsumer(queue)

        // Start receiving incoming messages
        connection.start()

        var receivedMessage: Message?
        do {
            println("Receiving message...")
            receivedMessage = consumer.receive(1000)
            val converted = convertMessage(receivedMessage)
            if (converted != null) {
                handler(converted)
            }
        } while (receivedMessage != null)
        println("No more messages to fetch")
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

}
