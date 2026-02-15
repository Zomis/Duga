package net.zomis.duga.chat

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory

data class BotConfig(val rootUrl: String, val chatUrl: String, val botEmail: String, val botPassword: String)

class StackExchangeLogin(private val httpClient: HttpClient, private val config: BotConfig) {

    private val logger = LoggerFactory.getLogger(StackExchangeLogin::class.java)

    suspend fun login(): Boolean {
        val loginPage: String = httpClient.get("${config.rootUrl}/users/login")
        val fkey = Jsoup.parse(loginPage).selectFirst("input[name='fkey']")!!.attr("value")
        println(fkey)

        val r = httpClient.post<String>(config.rootUrl + "/users/login") {
            body = FormDataContent(Parameters.build {
                append("email", config.botEmail)
                append("password", config.botPassword)
                append("fkey", fkey)
            })
        }
        println(r)
        println("-------")

        val currentUserHtml: String = httpClient.get(config.rootUrl + "/users/current")
        val jsoup = Jsoup.parse(currentUserHtml)
        println("User show new:")
        println(jsoup.select(".user-show-new"))
        println("------------")
        val result = jsoup.selectFirst(".js-inbox-button")
        return result != null
    }

    suspend fun fkeyReal(): String {
        val favoriteChatsHtml: String = httpClient.get<String>(config.chatUrl + "/chats/join/favorite")
        val jsoup = Jsoup.parse(favoriteChatsHtml)
        val fkey = jsoup.select("form").last()!!.selectFirst("#fkey")!!.attr("value")
        println(jsoup.select(".topbar-menu-links"))
        println("----------")

        println(fkey)
        return fkey
    }

}
