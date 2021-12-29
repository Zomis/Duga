package net.zomis.duga

import kotlinx.coroutines.CoroutineScope
import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import net.zomis.duga.utils.stackexchange.CommentsScanTask
import net.zomis.duga.utils.stackexchange.ProgrammersClassification
import net.zomis.duga.utils.stackexchange.QuestionScanTask
import org.slf4j.LoggerFactory

class DugaTasks(private val poster: DugaPoster, private val stackApi: StackExchangeApi) {
    private val questionScanTask = QuestionScanTask(poster, stackApi, "codereview")

    fun commentsScanTask(scope: CoroutineScope): CommentsScanTask {
        val programmersClassification = try {
            val trainingData = this::class.java.classLoader.getResource("trainingset-programmers-comments.txt")
            val source = trainingData?.readText()
            val lines = source?.split("\n")
            ProgrammersClassification.machineLearning(lines ?: emptyList())
        } catch (e: Exception) {
            LoggerFactory.getLogger(DugaTasks::class.java).warn("Unable to load machine learning classification", e)
            ProgrammersClassification.machineLearning(emptyList())
        }
        return CommentsScanTask(scope, stackApi, programmersClassification, poster)
    }

    suspend fun answerInvalidation() = questionScanTask.run()

}
