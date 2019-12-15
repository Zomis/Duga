package net.zomis.duga.tasks

import com.fasterxml.jackson.databind.JsonNode
import net.zomis.duga.aws.DugaMessage
import net.zomis.duga.utils.StackExchangeAPI
import java.io.IOException

class RatingDiffTask(private val room: String, private val site: String, private val users: List<String>) : DugaTask {

    private val stackApi = StackExchangeAPI()

    override fun perform(): List<DugaMessage> {
        try {
            val usersString = users.joinToString(";")
            val result = stackApi.apiCall("users/$usersString", site, "!23IYXA.sS8.otifg5Aq.2")
            val users = result["items"]
            if (users.size() != 2) {
                throw UnsupportedOperationException("Cannot check diff for anything other than two users")
            }

            val max = users.maxBy { it["reputation"].asInt() }!!
            val min = users.minBy { it["reputation"].asInt() }!!
            val str = StringBuilder()
            str.append(clearName(max["display_name"].asText()) + " vs. " + clearName(min["display_name"].asText()) + ": ")
            str.append(max["reputation"].asInt() - min["reputation"].asInt())
            str.append(" diff. ")
            diffStr(str, max, min, "Year") {it["reputation_change_year"].asInt()}
            diffStr(str, max, min, "Quarter") {it["reputation_change_quarter"].asInt()}
            diffStr(str, max, min, "Month") {it["reputation_change_month"].asInt()}
            diffStr(str, max, min, "Week") {it["reputation_change_week"].asInt()}
            diffStr(str, max, min, "Day") {it["reputation_change_day"].asInt()}
            return listOf(DugaMessage(room, str.toString()))
        } catch (e: IOException) {
            e.printStackTrace()
            return listOf()
        }
    }

    private fun clearName(dispName: String): String {
        var displayName = dispName
        while (displayName.contains("&#")) {
            var replacement = displayName.substring(displayName.indexOf("&#") + 2)
            try {
                replacement = replacement.substring(0, replacement.indexOf(';'))
                val ch = Integer.parseInt(replacement)
                displayName = displayName.replaceFirst("&#\\d+;", ch.toChar().toString())
            } catch (ex: RuntimeException) {
                displayName = displayName.replaceFirst("&#", "")
            }
        }
        return displayName
    }

    private fun diffStr(str: StringBuilder, max: JsonNode, min: JsonNode, string: String, function: (JsonNode) -> Int) {
        str.append(string)
        str.append(": ")
        val maxValue = function(max)
        val minValue = function(min)
        val diff = maxValue - minValue
        str.append(if (diff > 0) "+" else "")
        str.append(diff)
        str.append(". ")
    }

}
