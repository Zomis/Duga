package net.zomis.duga.features

import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.utils.github.GitHubApi
import net.zomis.duga.utils.github.HookString
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

class StackExchange(val poster: DugaPoster) {

    suspend fun weeklyUpdate() {
        val days = ChronoUnit.DAYS.between(LocalDate.of(1986, Month.SEPTEMBER, 22), LocalDate.now())
        val week = days / 7
        poster.postMessage("16134", "Has @Simon posted his weekly update? (Week $week, $days days)")
    }

    suspend fun starRace(hookString: HookString, gitHubApi: GitHubApi, repositories: List<String>) {
        val repositoryData = repositories.map { hookString.repo(it) to gitHubApi.stars(it) }
        val list = repositoryData.joinToString(" vs. ") {
            it.first + " ${it.second} stars"
        }
        // TODO: Discord poster
        poster.postMessage("20298", list)
    }

    suspend fun codeReviewUnanswered(stackExchangeApi: StackExchangeApi) {
        val siteStats = stackExchangeApi.unanswered("codereview")
        val percentageStr = String.format("%.4f", siteStats.percentageAnswered() * 100)
        val message = "***REFRESH!*** There are ${siteStats.unanswered} unanswered questions ($percentageStr answered)"
        poster.postMessage("8595", message)
    }

}