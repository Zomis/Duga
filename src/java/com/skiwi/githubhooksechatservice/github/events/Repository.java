
package com.skiwi.githubhooksechatservice.github.events;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Frank van Heeswijk
 */
public class Repository {
    @JsonProperty
    private long id;
    
    @JsonProperty
    private String name;
    
    @JsonProperty("full_name")
    private String fullName;
    
    @JsonProperty
    private SimpleUser owner;
    
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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public SimpleUser getOwner() {
        return owner;
    }

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFork() {
        return fork;
    }

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
}
