package net.zomis.duga

import grails.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

@Transactional(readOnly = true)
class DugaStats {

    private static final Logger logger = LoggerFactory.getLogger(DugaStats.class)

    @Autowired
    Environment environment

    @Transactional
    void addCommit(def repo, def commit) {
        def apiKey = environment.getProperty('githubAPI', '')
        int additions = 0
        int deletions = 0
        if (apiKey == '') {
            logger.error('No API Key set, skipping fetching of additions and deletions')
        } else {
            def user = new User()
            user.apiKey = apiKey
            String sha = commit.sha
            if (sha == null) {
                sha = commit.id
            }
            def json = user.github("repos/$repo.full_name/commits/$sha")
            additions = json.stats.additions
            deletions = json.stats.deletions
        }
        DailyInfo info = findOrCreate(repo)
        info.addCommits(1, additions, deletions)
        info.save(flush: true)
    }

    DailyInfo findOrCreate(repo) {
        DailyInfo info = DailyInfo.findByName(repo.full_name)
        if (info == null) {
            info = new DailyInfo()
            info.name = repo.full_name
            info.url = repo.html_url
        }
        info
    }

    @Transactional
    void addCommitBitbucket(def repo, def commit) {
        DailyInfo info = findOrCreateBitbucket(repo)
        info.addCommits(1, 0, 0)
        info.save(flush: true)
    }

    DailyInfo findOrCreateBitbucket(repo) {
        DailyInfo info = DailyInfo.findByName(repo.full_name)
        if (info == null) {
            info = new DailyInfo()
            info.name = repo.full_name
            info.url = repo.links.html.href
        }
        info
    }

    @Transactional
    void addIssueComment(def repo) {
        DailyInfo info = findOrCreate(repo)
        info.comments++
        info.save(flush: true)
    }

    @Transactional
    def addIssue(def repo, int delta) {
        DailyInfo info = findOrCreate(repo)
        int abs = Math.abs(delta)
        if (delta >= 0) {
            info.issuesOpened += abs
        } else {
            info.issuesClosed += abs
        }
        info.save(flush: true)
    }
}
