package net.zomis.duga.utils.stackexchange

import net.zomis.machlearn.common.LearningDataSet
import net.zomis.machlearn.regressionvectorized.GradientDescent
import net.zomis.machlearn.regressionvectorized.LogisticRegression
import net.zomis.machlearn.text.TextClassification
import net.zomis.machlearn.text.TextFeatureBuilder
import net.zomis.machlearn.text.TextFeatureMapper
import net.zomis.machlearn.text.TextFeatureWeights
import org.jblas.DoubleMatrix
import org.slf4j.LoggerFactory

import java.util.Random
import java.util.function.Predicate
import java.util.regex.Pattern

object ProgrammersClassification {

    private val logger = LoggerFactory.getLogger(ProgrammersClassification::class.java)

    private val PROG_LINK = Pattern.compile(Pattern.quote("<a href=\"http") + "s?"
            + Pattern.quote("://softwareengineering.stackexchange.com")
            + "(/|/help/.*)?" + Pattern.quote("\">"))

    private class ConvergenceIterations(private var count: Int): Predicate<DoubleArray> {
        override fun test(doubles: DoubleArray): Boolean = count-- == 0
    }

    fun machineLearning(lines: List<String>?): TextClassification {
        if (lines == null || lines.isEmpty()) {
            // Return a classifier that is only classifying as false and giving -1 score
            return TextClassification({it}, TextFeatureMapper("programmers"), doubleArrayOf(-1.0, 0.0), 0.5)
        }
        val textFeatures = TextFeatureBuilder(intArrayOf(1, 2), this::filter)
        val data = LearningDataSet()
        val processedStrings = mutableListOf<String>()
        for (str in lines) {
            if (!str.startsWith("0 ") && !str.startsWith("1 ")) {
                continue
            }
            val expected = str.startsWith("1")
            val text = str.substring(2)
            val processed = preprocessProgrammers(text)
            val expectedChar = if (expected) '1' else '0'
            processedStrings.add(expectedChar.toString() + processed)
            textFeatures.add(processed)
        }

        val mapper = textFeatures.mapper(500)
        for (str in processedStrings) {
            val expectTrue = str[0] == '1'
            data.add(str, mapper.toFeatures(str), if (expectTrue) 1.0 else 0.0)
        }
        val partitionedData = data.partition(0.6, 0.2, 0.2, Random(42))
        val trainingSet = partitionedData.trainingSet
        val crossValidSet = partitionedData.crossValidationSet
        val testSet = partitionedData.testSet

        val learnedT = GradientDescent.gradientDescent(
                DoubleMatrix(trainingSet.xs), DoubleMatrix(trainingSet.y),
                ConvergenceIterations(20000),
                doubleArrayOf(data.numFeaturesWithZero().toDouble()), 0.01)
        val learnedTheta = learnedT.toArray()
        val weights = TextFeatureWeights(mapper.getFeatures(), learnedTheta)
        weights.getMapByValue().forEach { obj -> logger.info(obj.toString()) }

        val cost = LogisticRegression.costFunction(trainingSet.getXs(), trainingSet.getY()).apply(learnedTheta)
        logger.info("Training Set Cost: $cost")

        val crossCost = LogisticRegression.costFunction(crossValidSet.getXs(), crossValidSet.getY()).apply(learnedTheta)
        logger.info("Validation Set Cost: $crossCost")

        val function: (DoubleArray, DoubleArray) -> Boolean = { theta, x ->
            LogisticRegression.hypothesis(theta, x) >= 0.3
        }

        logger.info("ALL Score: " + data.precisionRecallF1(learnedTheta, function))
        logger.info("Training Score: " + trainingSet.precisionRecallF1(learnedTheta, function))
        logger.info("CrossVal Score: " + crossValidSet.precisionRecallF1(learnedTheta, function))
        logger.info("TestSet  Score: " + testSet.precisionRecallF1(learnedTheta, function))

        val classification = TextClassification(this::preprocessProgrammers, mapper, learnedTheta, 0.4)
        return classification
    }

    private fun filter(feature: String, nGram: Int): Boolean {
        return feature.trim().length > (if (nGram > 1) 7 else 2)
    }

    private fun preprocessProgrammers(textOriginal: String): String {
        var text = textOriginal.toLowerCase()
        text = PROG_LINK.matcher(text).replaceAll("(link-to-programmers)")
        text = text.replace("<a href=\"([^\"]+)\">", "$1 ") // Extract links
        text = text.replace("<[^<>]+>", " ") // Remove HTML
        text = text.replace("\\d+", "(number)")
        text = text.replace("stack overflow", "stackoverflow")
        text = text.replace("stack exchange", "stackexchange")
        text = text.replace("programmers.stackexchange.com/q", "(progs-question) ")
        text = text.replace("programmers.stackexchange.com/t", "(progs-tag) ")
        text = text.replace("programmers.stackexchange.com/a", "(progs-answer) ")
        text = text.replace("softwareengineering.stackexchange.com/q", "(progs-question) ")
        text = text.replace("softwareengineering.stackexchange.com/t", "(progs-tag) ")
        text = text.replace("softwareengineering.stackexchange.com/a", "(progs-answer) ")
        text = text.replace("(http|https)://[^\\s]*", "(unclassified-httpaddr)")
        text = text.replace("[\\.?!,]", " ")
        text = text.replace("\\(number\\) (secs?|mins?) ago", "")

        text = text.replace("i'm ", "i am ")
        text = text.replace("we're ", "we are ")
        text = text.replace("i've ", "i have ")
        text = text.replace("you've ", "you have ")
        text = text.replace("you're ", "you are ")
        text = text.replace("i'll ", "i will ")
        text = text.replace("can't ", "can not ")
        text = text.replace("won't ", "will not ")
        text = text.replace("he's ", "he is ")
        text = text.replace("she's ", "she is ")
        text = text.replace("it's ", "it is ")
        text = text.replace("she'll ", "she will ")
        text = text.replace("he'll ", "he will ")
        text = text.replace("it'll ", "it will ")
        text = text.replace("what's ", "what is ")
        text = text.replace("who's ", "who is ")
        text = text.replace("shouldn't ", "should not ")
        text = text.replace("wouldn't ", "would not ")
        text = text.replace("couldn't ", "could not ")
        text = text.replace(" don't ", " do not ")
        return text.replace("\"", "")
    }

}
