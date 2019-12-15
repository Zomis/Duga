package net.zomis.duga.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URL
import java.util.zip.GZIPInputStream

class StackExchangeAPI {

    private val stackAPI = System.getenv("STACKEXCHANGE_API")

    private fun buildURL(apiCall: String, site: String, filter: String, apiKey: String): URL {
        var call = apiCall
        if (!call.contains("?")) {
            call += "?dummy"
        }
        return URL("https://api.stackexchange.com/2.2/" + call
                + "&site=" + site
                + "&filter=" + filter + "&key=" + apiKey)
    }

    fun apiCall(apiCall: String, site: String, filter: String): JsonNode {
        val apiKey = stackAPI
        try {
            val url = buildURL(apiCall, site, filter, apiKey)
            val connection = url.openConnection()
            connection.setRequestProperty("Accept-Encoding", "identity")
            val stream = GZIPInputStream(connection.getInputStream())
            return ObjectMapper().readTree(stream)
        } catch (ex: IOException) {
            val copy = IOException(ex.message?.replace(apiKey, "xxxxxxxxxxxxxxxx"), ex.cause)
            copy.stackTrace = ex.stackTrace
            throw copy
        }
    }


}