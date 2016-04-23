package net.zomis.duga.tasks

import net.zomis.machlearn.text.TextClassification

class DugaLearning {

    private final TextClassification classification
    private final String text

    DugaLearning(TextClassification classification, String text) {
        this.classification = classification
        this.text = text
    }

    String getProcessed() {
        return classification.preprocess(text);
    }

    Map<String, Double> getFeatures() {
        // run through text classification and find out which features this message has,
        // and which score contribution each feature has
        classification.getFeatures(getProcessed())
    }

    void classify(boolean classification) {

        println "Classify as " + classification
        // search for previous
        // make a commit with the new classification
        // to the training set data associated with the classification
    }

}
