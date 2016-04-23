package net.zomis.duga

import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.transport.PushResult
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class DugaGit implements InitializingBean {

    private static final Logger logger = LogManager.getLogger(DugaGit.class)

    public static final String CONFIG_GIT_BASE_DIR = "gitBaseDir";

    @Autowired
    Environment environment

    private File baseDir;

    @Override
    void afterPropertiesSet() throws Exception {
        String gitPath = environment.getProperty(CONFIG_GIT_BASE_DIR, "duga-git-worktrees");
        baseDir = new File(gitPath);
        if (baseDir.mkdirs()) {
            logger.info("Git base created: " + baseDir.getAbsolutePath())
        } else {
            logger.error("Git base could not be created: " + baseDir.getAbsolutePath())
        }
    }

    public Git cloneOrPull(String name, String uri) {
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            throw new FileNotFoundException("baseDir not found: " + baseDir.getAbsolutePath());
        }
        File repoDir = new File(baseDir, name);
        if (repoDir.exists()) {
            try {
                Git git = Git.open(repoDir);
                git.pull().call();
                return git;
            } catch (IOException ex) {
                logger.warn("Unable to open repository " + repoDir.getAbsolutePath() + ": " + ex)
            }
        }
        logger.info("Cloning " + uri + " into " + repoDir.getAbsolutePath())
        return Git.cloneRepository()
            .setURI(uri)
            .setDirectory(repoDir)
            .call()
    }

    public Iterable<PushResult> push(Git git) {
        String username = environment.getProperty("gitUsername")
        String password = environment.getProperty("gitPassword")
        return git.push()
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
            .call()
    }

    public PersonIdent getPersonIdent() {
        String name = environment.getProperty("gitCommitName", "Duga Bot")
        String email = environment.getProperty("gitCommitEmail", "zomisforsberg@gmail.com")
        return new PersonIdent(name, email);
    }

}
