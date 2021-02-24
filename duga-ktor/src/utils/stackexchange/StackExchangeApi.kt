package net.zomis.duga.utils.stackexchange

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.get
import io.ktor.client.request.*
import java.io.IOException
import java.net.URL

class StackExchangeApi(val httpClient: HttpClient, val apiKey: String?) {

    private val mapper = jacksonObjectMapper()

    suspend fun fetchComments(site: String, fromDate: Long): JsonNode? {
        val filter = "!Fcb8.PVyNbcSSIFtmbqhHwtwVw"
        return apiCall("comments?page=1&pagesize=100&fromdate=" + fromDate +
                "&order=desc&sort=creation", site, filter)
    }

    private fun buildURL(apiPath: String, site: String, filter: String, apiKey: String): URL {
        val apiCall = if (!apiPath.contains("?")) "$apiPath?dummy" else apiPath
        return URL("https://api.stackexchange.com/2.2/" + apiCall
                + "&site=" + site
                + "&filter=" + filter + "&key=" + apiKey)
    }

    suspend fun apiCall(apiCall: String, site: String, filter: String): JsonNode? {
        if (apiKey == null) return null
        try {
            val url = buildURL(apiCall, site, filter, apiKey)
            val s: String = httpClient.get(url) {

            }
            return mapper.readTree(s)
        } catch (ex: IOException) {
            val copy = IOException(ex.message?.replace(apiKey, "xxxxxxxxxxxxxxxx"), ex.cause)
            copy.stackTrace = ex.stackTrace
            throw copy
        }
    }

}