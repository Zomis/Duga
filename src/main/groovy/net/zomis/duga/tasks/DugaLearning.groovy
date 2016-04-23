package net.zomis.duga.tasks

import net.zomis.duga.DugaGit
import net.zomis.duga.chat.listen.ChatMessageIncoming
import net.zomis.machlearn.text.TextClassification
import org.eclipse.jgit.api.CheckoutCommand
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

import java.nio.file.Files
import java.util.stream.Stream

class DugaLearning {

    enum ClassificationResult {
        CLASSIFICATION_ADDED, ALREADY_EXISTS;
    }

    private final TextClassification classification
    private final String text
    private final ChatMessageIncoming chatMessage
    private final DugaGit git

    DugaLearning(TextClassification classification, String text, ChatMessageIncoming chatMessage, DugaGit git) {
        this.classification = classification
        this.text = text
        this.chatMessage = chatMessage
        this.git = git
    }

    String getProcessed() {
        return classification.preprocess(text);
    }

    Map<String, Double> getFeatures() {
        // run through text classification and find out which features this message has,
        // and which score contribution each feature has
        classification.getFeatures(getProcessed())
    }

    ClassificationResult classify(boolean classification) {
        Git repo = git.cloneOrPull("Duga", "https://github.com/Zomis/Duga.git")

        // checkout the specific branch
        String branchName = 'classification';
        boolean branchExists = repo.branchList().call().stream()
            .anyMatch({Ref ref -> ref.name.equals("refs/heads/" + branchName)})
        CheckoutCommand checkout = repo.checkout();
        if (!branchExists) {
            checkout = checkout.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setCreateBranch(true)
        }
        checkout.setName(branchName).call()

        // Open the training set file and see if the training data exists already
        String relativePath = "src/main/resources/trainingset-programmers-comments.txt";
        println repo.repository.workTree.absolutePath
        File file = new File(repo.repository.workTree, relativePath);
        Stream<String> stream = Files.lines(file.toPath())
        Optional<String> existing = stream.filter({str -> str.contains(text)}).findFirst();
        if (existing.isPresent()) {
            // search for previous, if one is found then return
            return ClassificationResult.ALREADY_EXISTS;
        }

        // Add line to the training set file
        def pw = new PrintWriter(new FileOutputStream(file, true))
        String classificationPrefix = classification ? "1 " : "0 ";
        String line = classificationPrefix + text
        pw.println line
        pw.close()

        repo.add().addFilepattern(relativePath).call()

        long messageId = chatMessage.getMessageId()
        String username = chatMessage.getUserName()
        String message = chatMessage.getContent()
        String commitMessage = """Add programmers classification training data

http://chat.stackexchange.com/transcript/message/$messageId#$messageId
$username said: $message
"""
        // make a commit with the new classification
        repo.commit()
            .setAuthor(git.personIdent)
            .setCommitter(git.personIdent)
            .setMessage(commitMessage)
            .call()

        git.push(repo)

        return ClassificationResult.CLASSIFICATION_ADDED;
    }

}
