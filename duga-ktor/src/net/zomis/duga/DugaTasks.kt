package net.zomis.duga

import net.zomis.duga.chat.DugaPoster
import net.zomis.duga.utils.stackexchange.StackExchangeApi
import net.zomis.duga.utils.stackexchange.CommentsScanTask
import net.zomis.duga.utils.stackexchange.ProgrammersClassification
import net.zomis.duga.utils.stackexchange.QuestionScanTask
import net.zomis.machlearn.text.TextClassification

class DugaTasks(private val poster: DugaPoster, private val stackApi: StackExchangeApi) {
    private val questionScanTask = QuestionScanTask(poster, stackApi, "codereview")
    private val programmersClassification: TextClassification
    private val commentsScanTask: CommentsScanTask

    init {
        val trainingData = this::class.java.classLoader.getResource("trainingset-programmers-comments.txt")
        val source = trainingData?.readText()
        val lines = source?.split("\n")
        this.programmersClassification = ProgrammersClassification.machineLearning(lines)
        this.commentsScanTask = CommentsScanTask(stackApi, programmersClassification, poster)
    }

    suspend fun commentScan() = commentsScanTask.run()
    suspend fun answerInvalidation() = questionScanTask.run()

}
