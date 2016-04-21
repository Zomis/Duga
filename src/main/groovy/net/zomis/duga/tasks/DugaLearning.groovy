package net.zomis.duga.tasks

import net.zomis.machlearn.text.TextClassification

class DugaLearning {

    DugaLearning(TextClassification classification, String text) {
        // fetch message content from URL
    }

    Map<String, Double> getFeatures() {
        // run through text classification and find out which features this message has,
        // and which score contribution each feature has
    }

    void classify(boolean classification) {
        // use github API to make a commit by appending a line
        // to the training set data associated with the classification
    }

}
