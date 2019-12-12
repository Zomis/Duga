package net.zomis.duga.aws

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.fasterxml.jackson.databind.ObjectMapper
import net.zomis.duga.chat.state.BotState

object DugaState {

    val mapper = ObjectMapper()

    fun readFromDB(dynamoDB: AmazonDynamoDB): BotState? {
        val fetchRequest = GetItemRequest("Duga-Bots", mapOf(
            "botname" to AttributeValue("duga"),
            "property" to AttributeValue("config")
        ))
        val getResult = dynamoDB.getItem(fetchRequest)
        val item = getResult.item ?: return null
        val stateString = item["state"]?.s ?: return null

        val stateAsString = mapper.readValue(stateString, BotState::class.java)
        return stateAsString
    }

    fun saveToDB(dynamoDB: AmazonDynamoDB, state: BotState) {
        val stateAsString = mapper.writeValueAsString(state)
        val request = PutItemRequest("Duga-Bots", mapOf(
            "botname" to AttributeValue("duga"),
            "property" to AttributeValue("config"),
            "state" to AttributeValue(stateAsString)
        ))
        dynamoDB.putItem(request)
    }


}