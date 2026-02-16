package net.zomis.duga.features

import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.utils.stats.DugaStats

class DugaFeatures(val poster: DugaPoster) {

    suspend fun dailyStats(stats: DugaStats, clearStats: Boolean) {
        val allStats = if (clearStats) stats.clearStats() else stats.currentStats()
        val messages = allStats.map { stat ->
            val values = stat.reset().toList()
                .joinToString(". ") { "${it.second} ${it.first}" }
            val group = stat.displayName
            val url = stat.url
            "\\[[**$group**]($url)\\] $values"
        }
        val rooms = listOf("16134")
        rooms.forEach { room ->
            val roomPoster = poster.room(room)
            roomPoster.post("***REFRESH!***")
            messages.forEach { message ->
                roomPoster.post(message)
            }
        }
    }

}