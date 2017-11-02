package net.zomis.duga.tasks;

import net.zomis.machlearn.common.ClassifierFunction;
import net.zomis.machlearn.common.LearningDataSet;
import net.zomis.machlearn.common.PartitionedDataSet;
import net.zomis.machlearn.regressionvectorized.GradientDescent;
import net.zomis.machlearn.regressionvectorized.LogisticRegression;
import net.zomis.machlearn.text.TextClassification;
import net.zomis.machlearn.text.TextFeatureBuilder;
import net.zomis.machlearn.text.TextFeatureMapper;
import net.zomis.machlearn.text.TextFeatureWeights;
import org.jblas.DoubleMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ProgrammersClassification {

    private static final Logger logger = LoggerFactory.getLogger(ProgrammersClassification.class);

    private static final Pattern PROG_LINK = Pattern.compile(Pattern.quote("<a href=\"http") + "s?"
            + Pattern.quote("://programmers.stackexchange.com")
            + "(/|/help/.*)?" + Pattern.quote("\">"));

    private static class ConvergenceIterations implements Predicate<double[]> {
        private int count;

        public ConvergenceIterations(int count) {
            this.count = count;
        }

        @Override
        public boolean test(double[] doubles) {
            return count-- == 0;
        }
    }

    public static TextClassification machineLearning(String[] lines) {
        if (lines == null || lines.length == 0) {
            // Return a classifier that is only classifying as false and giving -1 score
            return new TextClassification(s -> s, new TextFeatureMapper("programmers"), new double[]{-1, 0}, 0.5);
        }
        TextFeatureBuilder textFeatures = new TextFeatureBuilder(new int[]{1, 2},
            ProgrammersClassification::filter);
        LearningDataSet data = new LearningDataSet();
        List<String> processedStrings = new ArrayList<>();
        for (String str : lines) {
            if (!str.startsWith("0 ") && !str.startsWith("1 ")) {
                continue;
            }
            boolean expected = str.startsWith("1");
            String text = str.substring(2);
            String processed = preprocessProgrammers(text);
            char expectedChar = expected ? '1' : '0';
            processedStrings.add(expectedChar + processed);
            textFeatures.add(processed);
        }

        TextFeatureMapper mapper = textFeatures.mapper(500);
        for (String str : processedStrings) {
            boolean expectTrue = str.charAt(0) == '1';
            data.add(str, mapper.toFeatures(str), expectTrue ? 1 : 0);
        }
        PartitionedDataSet partitionedData = data.partition(0.6, 0.2, 0.2, new Random(42));
        LearningDataSet trainingSet = partitionedData.getTrainingSet();
        LearningDataSet crossValidSet = partitionedData.getCrossValidationSet();
        LearningDataSet testSet = partitionedData.getTestSet();

        DoubleMatrix learnedT = GradientDescent.gradientDescent(
                new DoubleMatrix(trainingSet.getXs()), new DoubleMatrix(trainingSet.getY()),
                new ConvergenceIterations(20000),
                new double[data.numFeaturesWithZero()], 0.01);
        double[] learnedTheta = learnedT.toArray();
        TextFeatureWeights weights = new TextFeatureWeights(mapper.getFeatures(), learnedTheta);
        weights.getMapByValue().forEach(obj -> logger.info(obj.toString()));

        double cost = LogisticRegression.costFunction(trainingSet.getXs(), trainingSet.getY()).apply(learnedTheta);
        logger.info("Training Set Cost: " + cost);

        double crossCost = LogisticRegression.costFunction(crossValidSet.getXs(), crossValidSet.getY()).apply(learnedTheta);
        logger.info("Validation Set Cost: " + crossCost);

        ClassifierFunction function = (theta, x) ->
                LogisticRegression.hypothesis(theta, x) >= 0.3;

        logger.info("ALL Score: " + data.precisionRecallF1(learnedTheta, function));
        logger.info("Training Score: " + trainingSet.precisionRecallF1(learnedTheta, function));
        logger.info("CrossVal Score: " + crossValidSet.precisionRecallF1(learnedTheta, function));
        logger.info("TestSet  Score: " + testSet.precisionRecallF1(learnedTheta, function));

        TextClassification classification = new TextClassification(
            ProgrammersClassification::preprocessProgrammers, mapper, learnedTheta, 0.4);
        return classification;
    }

    private static boolean filter(String feature, Integer nGram) {
        return feature.trim().length() > ((nGram > 1) ? 7 : 2);
    }

    private static String preprocessProgrammers(String text) {
        text = text.toLowerCase();
        text = PROG_LINK.matcher(text).replaceAll("(link-to-programmers)");
        text = text.replaceAll("<a href=\"([^\"]+)\">", "$1 "); // Extract links
        text = text.replaceAll("<[^<>]+>", " "); // Remove HTML
        text = text.replaceAll("\\d+", "(number)");
        text = text.replaceAll("stack overflow", "stackoverflow");
        text = text.replaceAll("stack exchange", "stackexchange");
        text = text.replaceAll("programmers.stackexchange.com/q", "(progs-question) ");
        text = text.replaceAll("programmers.stackexchange.com/t", "(progs-tag) ");
        text = text.replaceAll("programmers.stackexchange.com/a", "(progs-answer) ");
        text = text.replaceAll("(http|https)://[^\\s]*", "(unclassified-httpaddr)");
        text = text.replaceAll("[\\.?!,]", " ");
        text = text.replaceAll("\\(number\\) (secs?|mins?) ago", "");

        text = text.replaceAll("i'm ", "i am ");
        text = text.replaceAll("we're ", "we are ");
        text = text.replaceAll("i've ", "i have ");
        text = text.replaceAll("you've ", "you have ");
        text = text.replaceAll("you're ", "you are ");
        text = text.replaceAll("i'll ", "i will ");
        text = text.replaceAll("can't ", "can not ");
        text = text.replaceAll("won't ", "will not ");
        text = text.replaceAll("he's ", "he is ");
        text = text.replaceAll("she's ", "she is ");
        text = text.replaceAll("it's ", "it is ");
        text = text.replaceAll("she'll ", "she will ");
        text = text.replaceAll("he'll ", "he will ");
        text = text.replaceAll("it'll ", "it will ");
        text = text.replaceAll("what's ", "what is ");
        text = text.replaceAll("who's ", "who is ");
        text = text.replaceAll("shouldn't ", "should not ");
        text = text.replaceAll("wouldn't ", "would not ");
        text = text.replaceAll("couldn't ", "could not ");
        text = text.replaceAll(" don't ", " do not ");
        return text.replace("\"", "");

    }

}
