package net.zomis.duga

import grails.transaction.Transactional

@Transactional(readOnly = true)
class DugaStats {

    @Transactional
    void addCommits(repo, int commits) {
        DailyInfo info = findOrCreate(repo)
        info.addCommits(commits, 0, 0)
        info.save()
    }

    DailyInfo findOrCreate(repo) {
        DailyInfo info = DailyInfo.findByName(repo.full_name)
        if (info == null) {
            info = new DailyInfo()
            info.name = repo.full_name
            info.url = repo.url
        }
        info
    }
}
