package net.zomis.duga;

class DailyInfo {

	String name
	String url
	String comment = ''
	Integer commits = 0
	Integer issuesOpened = 0
	Integer issuesClosed = 0
	Integer additions = 0
	Integer deletions = 0
	Integer comments = 0
	
	void addIssues(int opened, int closed, int comments) {
		this.issuesOpened += opened;
		this.issuesClosed += closed;
		this.comments += comments;
	}
	
	void addCommits(int commits, int additions, int deletions) {
		this.commits += commits;
		this.additions += additions;
		this.deletions += deletions;
	}

    void reset() {
        this.issuesOpened = 0
        this.issuesClosed = 0
        this.commits = 0
        this.deletions = 0
        this.additions = 0
        this.comments = 0
    }
	
}
