
package com.skiwi.githubhooksechatservice.events.github.classes;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;

/**
 *
 * @author Frank van Heeswijk
 */
public final class LegacyRepository extends AnySetterJSONObject implements GithubRepository {
    @JsonProperty
    private long id;
    
    @JsonProperty
    private String name;
    
    @JsonProperty("full_name")
    private String fullName;
    
    @JsonProperty
    private LegacySimpleUser owner;
    
    @JsonProperty("private")
    private boolean isPrivate;
    
    @JsonProperty("html_url")
    private String htmlUrl;
    
    @JsonProperty
    private String description;
    
    @JsonProperty
    private boolean fork;
    
    @JsonProperty
    private String url;
    
    @JsonProperty("forks_url")
    private String forksUrl;
    
    @JsonProperty("keys_url")
    private String keysUrl;
    
    @JsonProperty("collaborators_url")
    private String collaboratorsUrl;
    
    @JsonProperty("teams_url")
    private String teamsUrl;
    
    @JsonProperty("hooks_url")
    private String hooksUrl;
    
    @JsonProperty("issue_events_url")
    private String issueEventsUrl;
    
    @JsonProperty("events_url")
    private String eventsUrl;
    
    @JsonProperty("assignees_url")
    private String assigneesUrl;
    
    @JsonProperty("branches_url")
    private String branchesUrl;
    
    @JsonProperty("tags_url")
    private String tagsUrl;
    
    @JsonProperty("blobs_url")
    private String blobsUrl;
    
    @JsonProperty("git_tags_url")
    private String gitTagsUrl;
    
    @JsonProperty("git_refs_url")
    private String gitRefsUrl;
    
    @JsonProperty("trees_url")
    private String treesUrl;
    
    @JsonProperty("statuses_url")
    private String statusesUrl;
    
    @JsonProperty("languages_url")
    private String languagesUrl;
    
    @JsonProperty("stargazers_url")
    private String stargazersUrl;
    
    @JsonProperty("contributors_url")
    private String contributorsUrl;
    
    @JsonProperty("subscribers_url")
    private String subscribersUrl;
    
    @JsonProperty("subscription_url")
    private String subscriptionUrl;
    
    @JsonProperty("commits_url")
    private String commitsUrl;
    
    @JsonProperty("git_commits_url")
    private String gitCommitsUrl;
    
    @JsonProperty("comments_url")
    private String commentsUrl;
    
    @JsonProperty("issue_comment_url")
    private String issueCommentUrl;
    
    @JsonProperty("contents_url")
    private String contentsUrl;
    
    @JsonProperty("compare_url")
    private String compareUrl;
    
    @JsonProperty("merges_url")
    private String mergesUrl;
    
    @JsonProperty("archive_url")
    private String archiveUrl;
    
    @JsonProperty("downloads_url")
    private String downloadsUrl;
    
    @JsonProperty("issues_url")
    private String issuesUrl;
    
    @JsonProperty("pulls_url")
    private String pullsUrl;
    
    @JsonProperty("milestones_url")
    private String milestonesUrl;
    
    @JsonProperty("notifications_url")
    private String notificationsUrl;
    
    @JsonProperty("labels_url")
    private String labelsUrl;
    
    @JsonProperty("releases_url")
    private String releasesUrl;
    
    @JsonProperty("created_at")
    private long createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @JsonProperty("pushed_at")
    private long pushedAt;
    
    @JsonProperty("git_url")
    private String gitUrl;
    
    @JsonProperty("ssh_url")
    private String sshUrl;
    
    @JsonProperty("clone_url")
    private String cloneUrl;
    
    @JsonProperty("svn_url")
    private String svnUrl;
    
    @JsonProperty
    private String homepage;
    
    @JsonProperty
    private long size;
    
    @JsonProperty("stargazers_count")
    private long stargazersCount;
    
    @JsonProperty("watchers_count")
    private long watchersCount;
    
    @JsonProperty
    private String language;
    
    @JsonProperty("has_issues")
    private boolean hasIssues;
    
    @JsonProperty("has_downloads")
    private boolean hasDownloads;
    
    @JsonProperty("has_wiki")
    private boolean hasWiki;
	
	@JsonProperty("has_pages")
	private boolean hasPages;
    
    @JsonProperty("forks_count")
    private long forksCount;
    
    @JsonProperty("mirror_url")
    private String mirrorUrl;
    
    @JsonProperty("open_issues_count")
    private long openIssuesCount;
    
    @JsonProperty
    private long forks;
    
    @JsonProperty("open_issues")
    private long openIssues;
    
    @JsonProperty
    private long watchers;
    
    @JsonProperty("default_branch")
    private String defaultBranch;
    
    @JsonProperty
    private long stargazers;
    
    @JsonProperty("master_branch")
    private String masterBranch;
	
	@JsonProperty(required = false)
	private String organization;

    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public LegacySimpleUser getOwner() {
        return owner;
    }

    public boolean isIsPrivate() {
        return isPrivate;
    }

    @Override
    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFork() {
        return fork;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public String getForksUrl() {
        return forksUrl;
    }

    public String getKeysUrl() {
        return keysUrl;
    }

    public String getCollaboratorsUrl() {
        return collaboratorsUrl;
    }

    public String getTeamsUrl() {
        return teamsUrl;
    }

    public String getHooksUrl() {
        return hooksUrl;
    }

    public String getIssueEventsUrl() {
        return issueEventsUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public String getAssigneesUrl() {
        return assigneesUrl;
    }

    public String getBranchesUrl() {
        return branchesUrl;
    }

    public String getTagsUrl() {
        return tagsUrl;
    }

    public String getBlobsUrl() {
        return blobsUrl;
    }

    public String getGitTagsUrl() {
        return gitTagsUrl;
    }

    public String getGitRefsUrl() {
        return gitRefsUrl;
    }

    public String getTreesUrl() {
        return treesUrl;
    }

    public String getStatusesUrl() {
        return statusesUrl;
    }

    public String getLanguagesUrl() {
        return languagesUrl;
    }

    public String getStargazersUrl() {
        return stargazersUrl;
    }

    public String getContributorsUrl() {
        return contributorsUrl;
    }

    public String getSubscribersUrl() {
        return subscribersUrl;
    }

    public String getSubscriptionUrl() {
        return subscriptionUrl;
    }

    public String getCommitsUrl() {
        return commitsUrl;
    }

    public String getGitCommitsUrl() {
        return gitCommitsUrl;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public String getIssueCommentUrl() {
        return issueCommentUrl;
    }

    public String getContentsUrl() {
        return contentsUrl;
    }

    public String getCompareUrl() {
        return compareUrl;
    }

    public String getMergesUrl() {
        return mergesUrl;
    }

    public String getArchiveUrl() {
        return archiveUrl;
    }

    public String getDownloadsUrl() {
        return downloadsUrl;
    }

    public String getIssuesUrl() {
        return issuesUrl;
    }

    public String getPullsUrl() {
        return pullsUrl;
    }

    public String getMilestonesUrl() {
        return milestonesUrl;
    }

    public String getNotificationsUrl() {
        return notificationsUrl;
    }

    public String getLabelsUrl() {
        return labelsUrl;
    }

    public String getReleasesUrl() {
        return releasesUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public long getPushedAt() {
        return pushedAt;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public String getSvnUrl() {
        return svnUrl;
    }

    public String getHomepage() {
        return homepage;
    }

    public long getSize() {
        return size;
    }

    public long getStargazersCount() {
        return stargazersCount;
    }

    public long getWatchersCount() {
        return watchersCount;
    }

    public String getLanguage() {
        return language;
    }

    public boolean hasIssues() {
        return hasIssues;
    }

    public boolean hasDownloads() {
        return hasDownloads;
    }

    public boolean hasWiki() {
        return hasWiki;
    }
	
	public boolean hasPages() {
		return hasPages;
	}

    public long getForksCount() {
        return forksCount;
    }

    public String getMirrorUrl() {
        return mirrorUrl;
    }

    public long getOpenIssuesCount() {
        return openIssuesCount;
    }

    public long getForks() {
        return forks;
    }

    public long getOpenIssues() {
        return openIssues;
    }

    public long getWatchers() {
        return watchers;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public long getStargazers() {
        return stargazers;
    }

    public String getMasterBranch() {
        return masterBranch;
    }
	
	public String getOrganization() {
		return organization;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + (int)(this.id ^ (this.id >>> 32));
		hash = 29 * hash + Objects.hashCode(this.name);
		hash = 29 * hash + Objects.hashCode(this.fullName);
		hash = 29 * hash + Objects.hashCode(this.owner);
		hash = 29 * hash + (this.isPrivate ? 1 : 0);
		hash = 29 * hash + Objects.hashCode(this.htmlUrl);
		hash = 29 * hash + Objects.hashCode(this.description);
		hash = 29 * hash + (this.fork ? 1 : 0);
		hash = 29 * hash + Objects.hashCode(this.url);
		hash = 29 * hash + Objects.hashCode(this.forksUrl);
		hash = 29 * hash + Objects.hashCode(this.keysUrl);
		hash = 29 * hash + Objects.hashCode(this.collaboratorsUrl);
		hash = 29 * hash + Objects.hashCode(this.teamsUrl);
		hash = 29 * hash + Objects.hashCode(this.hooksUrl);
		hash = 29 * hash + Objects.hashCode(this.issueEventsUrl);
		hash = 29 * hash + Objects.hashCode(this.eventsUrl);
		hash = 29 * hash + Objects.hashCode(this.assigneesUrl);
		hash = 29 * hash + Objects.hashCode(this.branchesUrl);
		hash = 29 * hash + Objects.hashCode(this.tagsUrl);
		hash = 29 * hash + Objects.hashCode(this.blobsUrl);
		hash = 29 * hash + Objects.hashCode(this.gitTagsUrl);
		hash = 29 * hash + Objects.hashCode(this.gitRefsUrl);
		hash = 29 * hash + Objects.hashCode(this.treesUrl);
		hash = 29 * hash + Objects.hashCode(this.statusesUrl);
		hash = 29 * hash + Objects.hashCode(this.languagesUrl);
		hash = 29 * hash + Objects.hashCode(this.stargazersUrl);
		hash = 29 * hash + Objects.hashCode(this.contributorsUrl);
		hash = 29 * hash + Objects.hashCode(this.subscribersUrl);
		hash = 29 * hash + Objects.hashCode(this.subscriptionUrl);
		hash = 29 * hash + Objects.hashCode(this.commitsUrl);
		hash = 29 * hash + Objects.hashCode(this.gitCommitsUrl);
		hash = 29 * hash + Objects.hashCode(this.commentsUrl);
		hash = 29 * hash + Objects.hashCode(this.issueCommentUrl);
		hash = 29 * hash + Objects.hashCode(this.contentsUrl);
		hash = 29 * hash + Objects.hashCode(this.compareUrl);
		hash = 29 * hash + Objects.hashCode(this.mergesUrl);
		hash = 29 * hash + Objects.hashCode(this.archiveUrl);
		hash = 29 * hash + Objects.hashCode(this.downloadsUrl);
		hash = 29 * hash + Objects.hashCode(this.issuesUrl);
		hash = 29 * hash + Objects.hashCode(this.pullsUrl);
		hash = 29 * hash + Objects.hashCode(this.milestonesUrl);
		hash = 29 * hash + Objects.hashCode(this.notificationsUrl);
		hash = 29 * hash + Objects.hashCode(this.labelsUrl);
		hash = 29 * hash + Objects.hashCode(this.releasesUrl);
		hash = 29 * hash + (int)(this.createdAt ^ (this.createdAt >>> 32));
		hash = 29 * hash + Objects.hashCode(this.updatedAt);
		hash = 29 * hash + (int)(this.pushedAt ^ (this.pushedAt >>> 32));
		hash = 29 * hash + Objects.hashCode(this.gitUrl);
		hash = 29 * hash + Objects.hashCode(this.sshUrl);
		hash = 29 * hash + Objects.hashCode(this.cloneUrl);
		hash = 29 * hash + Objects.hashCode(this.svnUrl);
		hash = 29 * hash + Objects.hashCode(this.homepage);
		hash = 29 * hash + (int)(this.size ^ (this.size >>> 32));
		hash = 29 * hash + (int)(this.stargazersCount ^ (this.stargazersCount >>> 32));
		hash = 29 * hash + (int)(this.watchersCount ^ (this.watchersCount >>> 32));
		hash = 29 * hash + Objects.hashCode(this.language);
		hash = 29 * hash + (this.hasIssues ? 1 : 0);
		hash = 29 * hash + (this.hasDownloads ? 1 : 0);
		hash = 29 * hash + (this.hasWiki ? 1 : 0);
		hash = 29 * hash + (this.hasPages ? 1 : 0);
		hash = 29 * hash + (int)(this.forksCount ^ (this.forksCount >>> 32));
		hash = 29 * hash + Objects.hashCode(this.mirrorUrl);
		hash = 29 * hash + (int)(this.openIssuesCount ^ (this.openIssuesCount >>> 32));
		hash = 29 * hash + (int)(this.forks ^ (this.forks >>> 32));
		hash = 29 * hash + (int)(this.openIssues ^ (this.openIssues >>> 32));
		hash = 29 * hash + (int)(this.watchers ^ (this.watchers >>> 32));
		hash = 29 * hash + Objects.hashCode(this.defaultBranch);
		hash = 29 * hash + (int)(this.stargazers ^ (this.stargazers >>> 32));
		hash = 29 * hash + Objects.hashCode(this.masterBranch);
		hash = 29 * hash + Objects.hashCode(this.organization);
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LegacyRepository other = (LegacyRepository)obj;
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.fullName, other.fullName)) {
			return false;
		}
		if (!Objects.equals(this.owner, other.owner)) {
			return false;
		}
		if (this.isPrivate != other.isPrivate) {
			return false;
		}
		if (!Objects.equals(this.htmlUrl, other.htmlUrl)) {
			return false;
		}
		if (!Objects.equals(this.description, other.description)) {
			return false;
		}
		if (this.fork != other.fork) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.forksUrl, other.forksUrl)) {
			return false;
		}
		if (!Objects.equals(this.keysUrl, other.keysUrl)) {
			return false;
		}
		if (!Objects.equals(this.collaboratorsUrl, other.collaboratorsUrl)) {
			return false;
		}
		if (!Objects.equals(this.teamsUrl, other.teamsUrl)) {
			return false;
		}
		if (!Objects.equals(this.hooksUrl, other.hooksUrl)) {
			return false;
		}
		if (!Objects.equals(this.issueEventsUrl, other.issueEventsUrl)) {
			return false;
		}
		if (!Objects.equals(this.eventsUrl, other.eventsUrl)) {
			return false;
		}
		if (!Objects.equals(this.assigneesUrl, other.assigneesUrl)) {
			return false;
		}
		if (!Objects.equals(this.branchesUrl, other.branchesUrl)) {
			return false;
		}
		if (!Objects.equals(this.tagsUrl, other.tagsUrl)) {
			return false;
		}
		if (!Objects.equals(this.blobsUrl, other.blobsUrl)) {
			return false;
		}
		if (!Objects.equals(this.gitTagsUrl, other.gitTagsUrl)) {
			return false;
		}
		if (!Objects.equals(this.gitRefsUrl, other.gitRefsUrl)) {
			return false;
		}
		if (!Objects.equals(this.treesUrl, other.treesUrl)) {
			return false;
		}
		if (!Objects.equals(this.statusesUrl, other.statusesUrl)) {
			return false;
		}
		if (!Objects.equals(this.languagesUrl, other.languagesUrl)) {
			return false;
		}
		if (!Objects.equals(this.stargazersUrl, other.stargazersUrl)) {
			return false;
		}
		if (!Objects.equals(this.contributorsUrl, other.contributorsUrl)) {
			return false;
		}
		if (!Objects.equals(this.subscribersUrl, other.subscribersUrl)) {
			return false;
		}
		if (!Objects.equals(this.subscriptionUrl, other.subscriptionUrl)) {
			return false;
		}
		if (!Objects.equals(this.commitsUrl, other.commitsUrl)) {
			return false;
		}
		if (!Objects.equals(this.gitCommitsUrl, other.gitCommitsUrl)) {
			return false;
		}
		if (!Objects.equals(this.commentsUrl, other.commentsUrl)) {
			return false;
		}
		if (!Objects.equals(this.issueCommentUrl, other.issueCommentUrl)) {
			return false;
		}
		if (!Objects.equals(this.contentsUrl, other.contentsUrl)) {
			return false;
		}
		if (!Objects.equals(this.compareUrl, other.compareUrl)) {
			return false;
		}
		if (!Objects.equals(this.mergesUrl, other.mergesUrl)) {
			return false;
		}
		if (!Objects.equals(this.archiveUrl, other.archiveUrl)) {
			return false;
		}
		if (!Objects.equals(this.downloadsUrl, other.downloadsUrl)) {
			return false;
		}
		if (!Objects.equals(this.issuesUrl, other.issuesUrl)) {
			return false;
		}
		if (!Objects.equals(this.pullsUrl, other.pullsUrl)) {
			return false;
		}
		if (!Objects.equals(this.milestonesUrl, other.milestonesUrl)) {
			return false;
		}
		if (!Objects.equals(this.notificationsUrl, other.notificationsUrl)) {
			return false;
		}
		if (!Objects.equals(this.labelsUrl, other.labelsUrl)) {
			return false;
		}
		if (!Objects.equals(this.releasesUrl, other.releasesUrl)) {
			return false;
		}
		if (this.createdAt != other.createdAt) {
			return false;
		}
		if (!Objects.equals(this.updatedAt, other.updatedAt)) {
			return false;
		}
		if (this.pushedAt != other.pushedAt) {
			return false;
		}
		if (!Objects.equals(this.gitUrl, other.gitUrl)) {
			return false;
		}
		if (!Objects.equals(this.sshUrl, other.sshUrl)) {
			return false;
		}
		if (!Objects.equals(this.cloneUrl, other.cloneUrl)) {
			return false;
		}
		if (!Objects.equals(this.svnUrl, other.svnUrl)) {
			return false;
		}
		if (!Objects.equals(this.homepage, other.homepage)) {
			return false;
		}
		if (this.size != other.size) {
			return false;
		}
		if (this.stargazersCount != other.stargazersCount) {
			return false;
		}
		if (this.watchersCount != other.watchersCount) {
			return false;
		}
		if (!Objects.equals(this.language, other.language)) {
			return false;
		}
		if (this.hasIssues != other.hasIssues) {
			return false;
		}
		if (this.hasDownloads != other.hasDownloads) {
			return false;
		}
		if (this.hasWiki != other.hasWiki) {
			return false;
		}
		if (this.hasPages != other.hasPages) {
			return false;
		}
		if (this.forksCount != other.forksCount) {
			return false;
		}
		if (!Objects.equals(this.mirrorUrl, other.mirrorUrl)) {
			return false;
		}
		if (this.openIssuesCount != other.openIssuesCount) {
			return false;
		}
		if (this.forks != other.forks) {
			return false;
		}
		if (this.openIssues != other.openIssues) {
			return false;
		}
		if (this.watchers != other.watchers) {
			return false;
		}
		if (!Objects.equals(this.defaultBranch, other.defaultBranch)) {
			return false;
		}
		if (this.stargazers != other.stargazers) {
			return false;
		}
		if (!Objects.equals(this.masterBranch, other.masterBranch)) {
			return false;
		}
		if (!Objects.equals(this.organization, other.organization)) {
			return false;
		}
		return true;
	}
}
