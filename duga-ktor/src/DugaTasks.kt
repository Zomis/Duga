package net.zomis.duga

import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import net.zomis.duga.utils.stackexchange.CommentsScanTask
import net.zomis.duga.utils.stackexchange.DynamoDbCommentsScanTaskData
import net.zomis.duga.utils.stackexchange.InMemoryAnswerInvalidationCheckData
import net.zomis.duga.utils.stackexchange.InMemoryCommentsScanTaskData
import net.zomis.duga.utils.stackexchange.ProgrammersClassification
import net.zomis.duga.utils.stackexchange.QuestionScanTask
import net.zomis.machlearn.text.TextClassification
import org.slf4j.LoggerFactory

class DugaTasks(private val poster: DugaPoster, private val stackApi: StackExchangeApi) {
    private val questionScanTask = QuestionScanTask(poster, stackApi, "codereview",
        InMemoryAnswerInvalidationCheckData()
    )

    fun commentsScanTask(): CommentsScanTask {
        val programmersClassification = try {
            val trainingData = this::class.java.classLoader.getResource("trainingset-programmers-comments.txt")
            val source = trainingData?.readText()
            val lines = source?.split("\n")
            ProgrammersClassification.machineLearning(lines ?: emptyList())
        } catch (e: Exception) {
            LoggerFactory.getLogger(DugaTasks::class.java).warn("Unable to load machine learning classification", e)
            ProgrammersClassification.machineLearning(emptyList())
        }
        return CommentsScanTask(stackApi, programmersClassification, InMemoryCommentsScanTaskData(), poster)
    }

    fun commentScanPretrained(pretrained: TextClassification): CommentsScanTask {
        return CommentsScanTask(stackApi, pretrained, DynamoDbCommentsScanTaskData(), poster)
    }

    suspend fun answerInvalidation() = questionScanTask.run()

}
