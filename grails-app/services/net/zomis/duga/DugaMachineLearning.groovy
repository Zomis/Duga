package net.zomis.duga

import net.zomis.duga.tasks.ProgrammersClassification
import net.zomis.machlearn.text.TextClassification
import org.springframework.beans.factory.InitializingBean

class DugaMachineLearning implements InitializingBean {

    TextClassification programmers

    @Override
    void afterPropertiesSet() throws Exception {
        URL trainingData = getClass().getClassLoader()
            .getResource("trainingset-programmers-comments.txt");
        String source = trainingData?.text;
        String[] lines = source?.split("\n");
        this.programmers = ProgrammersClassification.machineLearning(lines);
    }

}
