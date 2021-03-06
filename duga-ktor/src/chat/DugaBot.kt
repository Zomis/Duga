package net.zomis.duga.chat

import io.ktor.client.*

class DugaBot(
    val httpClient: HttpClient,
    private val config: BotConfig,
    private val fkeyFunction: suspend (HttpClient, BotConfig) -> String
) {

    val chatUrl: String get() = config.chatUrl
    private var fkey: String? = null

    suspend fun fkey(): String {
        if (fkey == null) {
            refreshFKey()
        }
        return this.fkey!!
    }

    suspend fun refreshFKey() {
        fkey = fkeyFunction(httpClient, config)
    }

}
