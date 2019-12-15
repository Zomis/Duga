package net.zomis.duga.tasks

import com.fasterxml.jackson.databind.JsonNode
import net.zomis.duga.utils.StackExchangeAPI

class CommentScanTask {

    // Requires learning
    fun fetchComments(site: String, fromDate: Long): JsonNode {
        val filter = "!Fcb8.PVyNbcSSIFtmbqhHwtwVw"
        return StackExchangeAPI().apiCall("comments?page=1&pagesize=100&fromdate=" + fromDate +
                "&order=desc&sort=creation", site, filter)
    }

}