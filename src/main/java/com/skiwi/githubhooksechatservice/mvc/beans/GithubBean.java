package com.skiwi.githubhooksechatservice.mvc.beans;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;
import com.skiwi.githubhooksechatservice.events.github.CommitCommentEvent;
import com.skiwi.githubhooksechatservice.events.github.CreateEvent;
import com.skiwi.githubhooksechatservice.events.github.DeleteEvent;
import com.skiwi.githubhooksechatservice.events.github.ForkEvent;
import com.skiwi.githubhooksechatservice.events.github.GollumEvent;
import com.skiwi.githubhooksechatservice.events.github.IssueCommentEvent;
import com.skiwi.githubhooksechatservice.events.github.IssuesEvent;
import com.skiwi.githubhooksechatservice.events.github.MemberEvent;
import com.skiwi.githubhooksechatservice.events.github.PullRequestEvent;
import com.skiwi.githubhooksechatservice.events.github.PullRequestReviewCommentEvent;
import com.skiwi.githubhooksechatservice.events.github.PushEvent;
import com.skiwi.githubhooksechatservice.events.github.TeamAddEvent;
import com.skiwi.githubhooksechatservice.events.github.WatchEvent;
import com.skiwi.githubhooksechatservice.events.github.classes.Commit;
import com.skiwi.githubhooksechatservice.events.github.classes.LegacyCommit;
import com.skiwi.githubhooksechatservice.events.github.classes.WikiPage;

public class GithubBean {
	
    public AbstractEvent[] fetchRepoEvents(String name) {
    	ObjectMapper mapper = new ObjectMapper(); // just need one
    	try {
    		URL url = new URL("https://api.github.com/repos/" + name + "/events");
			AbstractEvent[] data = mapper.readValue(url, AbstractEvent[].class);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

	public String stringify(CommitCommentEvent commitCommentEvent) {
		if (commitCommentEvent.getComment().getPath() == null) {
			return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented]({4}) on commit [**{5}**]({6})",
					commitCommentEvent.getRepository().getFullName(),
					commitCommentEvent.getRepository().getHtmlUrl(),
					commitCommentEvent.getSender().getLogin(),
					commitCommentEvent.getSender().getHtmlUrl(),
					commitCommentEvent.getComment().getHtmlUrl(),
					commitCommentEvent.getComment().getCommitId().substring(0, 8),
					commitCommentEvent.getRepository().getHtmlUrl() + "/commit/" + commitCommentEvent.getComment().getCommitId());
		}
		else {
			return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented on **{4}**]({5}) of commit [**{6}**]({7})",
				commitCommentEvent.getRepository().getFullName(),
				commitCommentEvent.getRepository().getHtmlUrl(),
				commitCommentEvent.getSender().getLogin(),
				commitCommentEvent.getSender().getHtmlUrl(),
				commitCommentEvent.getComment().getPath(),
				commitCommentEvent.getComment().getHtmlUrl(),
				commitCommentEvent.getComment().getCommitId().substring(0, 8),
				commitCommentEvent.getRepository().getHtmlUrl() + "/commit/" + commitCommentEvent.getComment().getCommitId());
		}
	}

	public String stringify(CreateEvent createEvent) {
		String refUrl = null;
		switch (createEvent.getRefType()) {
			case "branch":
				refUrl = createEvent.getRepository().getHtmlUrl() + "/tree/" + createEvent.getRef();
				break;
			case "tag":
				refUrl = createEvent.getRepository().getHtmlUrl() + "/releases/tag/" + createEvent.getRef();
				break;
		}
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created {4} [**{5}**]({6})",
			createEvent.getRepository().getFullName(),
			createEvent.getRepository().getHtmlUrl(),
			createEvent.getSender().getLogin(),
			createEvent.getSender().getHtmlUrl(),
			createEvent.getRefType(),
			createEvent.getRef(),
			refUrl);
	}

	public String stringify(DeleteEvent deleteEvent) {
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) deleted {4} **{5}**",
			deleteEvent.getRepository().getFullName(),
			deleteEvent.getRepository().getHtmlUrl(),
			deleteEvent.getSender().getLogin(),
			deleteEvent.getSender().getHtmlUrl(),
			deleteEvent.getRefType(),
			deleteEvent.getRef());
	}

	public String stringify(ForkEvent forkEvent) {
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) forked us into [**{4}**]({5})",
			forkEvent.getRepository().getFullName(),
			forkEvent.getRepository().getHtmlUrl(),
			forkEvent.getSender().getLogin(),
			forkEvent.getSender().getHtmlUrl(),
			forkEvent.getForkee().getFullName(),
			forkEvent.getForkee().getHtmlUrl());
	}

	public String stringify(GollumEvent gollumEvent, WikiPage wikiPage) {
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) {4} wiki page [**{5}**]({6})",
			gollumEvent.getRepository().getFullName(),
			gollumEvent.getRepository().getHtmlUrl(),
			gollumEvent.getSender().getLogin(),
			gollumEvent.getSender().getHtmlUrl(),
			wikiPage.getAction(),
			wikiPage.getTitle().trim(),
			wikiPage.getHtmlUrl());
	}

	public String stringify(IssuesEvent issuesEvent) {
		switch (issuesEvent.getAction()) {
			case "assigned":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) assigned [**{4}**]({5}) to issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getAssignee().getLogin(),
					issuesEvent.getAssignee().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl());
			case "unassigned":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) unassigned [**{4}**]({5}) from issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getAssignee().getLogin(),
					issuesEvent.getAssignee().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl());
			case "labeled":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added label [**{4}**]({5}) to issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getLabel().getName(),
					issuesEvent.getRepository().getHtmlUrl() + "/labels/" + issuesEvent.getLabel().getName().replace(" ", "%20"),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl());
			case "unlabeled":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) removed label [**{4}**]({5}) from issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getLabel().getName(),
					issuesEvent.getRepository().getHtmlUrl() + "/labels/" + issuesEvent.getLabel().getName().replace(" ", "%20"),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl());
			case "opened":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) opened issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl());
			case "closed":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) closed issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl());
			case "reopened":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) reopened issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl());
			default:
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) " + issuesEvent.getAction() + " issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl());
		}
	}

	public String stringify(IssueCommentEvent issueCommentEvent) {
		String commentTarget = (issueCommentEvent.getIssue().getPullRequest() == null) ? "issue" : "pull request";
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented]({4}) on {5} [**#{6}: {7}**]({8})",
			issueCommentEvent.getRepository().getFullName(),
			issueCommentEvent.getRepository().getHtmlUrl(),
			issueCommentEvent.getSender().getLogin(),
			issueCommentEvent.getSender().getHtmlUrl(),
			issueCommentEvent.getComment().getHtmlUrl(),
			commentTarget,
			issueCommentEvent.getIssue().getNumber(),
			issueCommentEvent.getIssue().getTitle().trim(),
			issueCommentEvent.getIssue().getHtmlUrl());
	}

	public String stringify(MemberEvent memberEvent) {
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) {4} [**{5}**]({6})",
			memberEvent.getRepository().getFullName(),
			memberEvent.getRepository().getHtmlUrl(),
			memberEvent.getSender().getLogin(),
			memberEvent.getSender().getHtmlUrl(),
			memberEvent.getAction(),
			memberEvent.getMember().getLogin(),
			memberEvent.getMember().getHtmlUrl());
	}
	
	public String stringify(PullRequestReviewCommentEvent pullRequestReviewCommentEvent) {
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented on **{4}**]({5}) of pull request [**#{6}: {7}**]({8})",
			pullRequestReviewCommentEvent.getRepository().getFullName(),
			pullRequestReviewCommentEvent.getRepository().getHtmlUrl(),
			pullRequestReviewCommentEvent.getSender().getLogin(),
			pullRequestReviewCommentEvent.getSender().getHtmlUrl(),
			pullRequestReviewCommentEvent.getComment().getPath(),
			pullRequestReviewCommentEvent.getComment().getHtmlUrl(),
			pullRequestReviewCommentEvent.getPullRequest().getNumber(),
			pullRequestReviewCommentEvent.getPullRequest().getTitle().trim(),
			pullRequestReviewCommentEvent.getPullRequest().getHtmlUrl());
	}

	public String stringify(PullRequestEvent pullRequestEvent) {
		Commit head = pullRequestEvent.getPullRequest().getHead();
		Commit base = pullRequestEvent.getPullRequest().getBase();
		String headText;
		String baseText;
		if (head.getRepo().equals(base.getRepo())) {
			headText = head.getRef();
			baseText = base.getRef();
		}
		else {
			headText = head.getRepo().getFullName() + "/" + head.getRef();
			baseText = base.getRepo().getFullName() + "/" + base.getRef();
		}
		switch (pullRequestEvent.getAction()) {
			case "assigned":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) assigned [**{4}**]({5}) to pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getAssignee().getLogin(),
					pullRequestEvent.getAssignee().getHtmlUrl(),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle().trim(),
					pullRequestEvent.getPullRequest().getHtmlUrl());
			case "unassigned":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) unassigned [**{4}**]({5}) from pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getAssignee().getLogin(),
					pullRequestEvent.getAssignee().getHtmlUrl(),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle().trim(),
					pullRequestEvent.getPullRequest().getHtmlUrl());
			case "labeled":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added label [**{4}**]({5}) to pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getLabel().getName(),
					pullRequestEvent.getRepository().getHtmlUrl() + "/labels/" + pullRequestEvent.getLabel().getName().replace(" ", "%20"),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle().trim(),
					pullRequestEvent.getPullRequest().getHtmlUrl());
			case "unlabeled":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) removed label [**{4}**]({5}) from pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getLabel().getName(),
					pullRequestEvent.getRepository().getHtmlUrl() + "/labels/" + pullRequestEvent.getLabel().getName().replace(" ", "%20"),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle().trim(),
					pullRequestEvent.getPullRequest().getHtmlUrl());
			case "opened":
				if (pullRequestEvent.getPullRequest().getBody() == null || pullRequestEvent.getPullRequest().getBody().isEmpty()) {
					return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created pull request [**#{4}: {5}**]({6}) to merge [**{7}**]({8}) into [**{9}**]({10})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle().trim(),
							pullRequestEvent.getPullRequest().getHtmlUrl(),
							headText,
							head.getRepo().getHtmlUrl() + "/tree/" + head.getRef(),
							baseText,
							base.getRepo().getHtmlUrl() + "/tree/" + base.getRef());
				}
				else {
					return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created pull request [**#{4}: {5}**]({6}) to merge [**{7}**]({8}) into [**{9}**]({10})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle().trim(),
							pullRequestEvent.getPullRequest().getHtmlUrl(),
							headText,
							head.getRepo().getHtmlUrl() + "/tree/" + head.getRef(),
							baseText,
							base.getRepo().getHtmlUrl() + "/tree/" + base.getRef(),
						"> " + pullRequestEvent.getPullRequest().getBody());
				}
			case "closed":
				if (pullRequestEvent.getPullRequest().isMerged()) {
					return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) merged pull request [**#{4}: {5}**]({6}) from [**{7}**]({8}) into [**{9}**]({10})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle().trim(),
							pullRequestEvent.getPullRequest().getHtmlUrl(),
							headText,
							head.getRepo().getHtmlUrl() + "/tree/" + head.getRef(),
							baseText,
							base.getRepo().getHtmlUrl() + "/tree/" + base.getRef());
				}
				else {
					return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) rejected pull request [**#{4}: {5}**]({6})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle().trim(),
							pullRequestEvent.getPullRequest().getHtmlUrl());
				}
			case "reopened":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) reopened pull request [**#{4}: {5}**]({6})",
						pullRequestEvent.getRepository().getFullName(),
						pullRequestEvent.getRepository().getHtmlUrl(),
						pullRequestEvent.getSender().getLogin(),
						pullRequestEvent.getSender().getHtmlUrl(),
						pullRequestEvent.getPullRequest().getNumber(),
						pullRequestEvent.getPullRequest().getTitle().trim(),
						pullRequestEvent.getPullRequest().getHtmlUrl());
			case "synchronize":
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) synchronized pull request [**#{4}: {5}**]({6})",
						pullRequestEvent.getRepository().getFullName(),
						pullRequestEvent.getRepository().getHtmlUrl(),
						pullRequestEvent.getSender().getLogin(),
						pullRequestEvent.getSender().getHtmlUrl(),
						pullRequestEvent.getPullRequest().getNumber(),
						pullRequestEvent.getPullRequest().getTitle().trim(),
						pullRequestEvent.getPullRequest().getHtmlUrl());
			default:
				return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) " + pullRequestEvent.getAction() +
						" pull request [**#{4}: {5}**]({6})",
						pullRequestEvent.getRepository().getFullName(),
						pullRequestEvent.getRepository().getHtmlUrl(),
						pullRequestEvent.getSender().getLogin(),
						pullRequestEvent.getSender().getHtmlUrl(),
						pullRequestEvent.getPullRequest().getNumber(),
						pullRequestEvent.getPullRequest().getTitle().trim(),
						pullRequestEvent.getPullRequest().getHtmlUrl());
		}
	}

	public String stringify(WatchEvent watchEvent) {
		String action = watchEvent.getAction().equals("started") ? "starred" : watchEvent.getAction();
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) " + action + " us",
				watchEvent.getRepository().getFullName(),
				watchEvent.getRepository().getHtmlUrl(),
				watchEvent.getSender().getLogin(),
				watchEvent.getSender().getHtmlUrl());
	}

	public String stringify(TeamAddEvent teamAddEvent) {
		if (teamAddEvent.getUser() == null) {
			return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added us to team [**{4}**]({5})",
				teamAddEvent.getRepository().getFullName(),
				teamAddEvent.getRepository().getHtmlUrl(),
				teamAddEvent.getSender().getLogin(),
				teamAddEvent.getSender().getHtmlUrl(),
				teamAddEvent.getTeam().getName(),
				teamAddEvent.getSender().getHtmlUrl() + "/" + teamAddEvent.getTeam().getName());
		}
		else {
			return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added [**{4}**]({5}) to team [**{6}**]({7})",
				teamAddEvent.getRepository().getFullName(),
				teamAddEvent.getRepository().getHtmlUrl(),
				teamAddEvent.getSender().getLogin(),
				teamAddEvent.getSender().getHtmlUrl(),
				teamAddEvent.getUser().getLogin(),
				teamAddEvent.getUser().getHtmlUrl(),
				teamAddEvent.getTeam().getName(),
				teamAddEvent.getSender().getHtmlUrl() + "/" + teamAddEvent.getTeam().getName());
		}
	}

	public String stringify(PushEvent pushEvent, LegacyCommit commit) {
		String branch = pushEvent.getRef().replace("refs/heads/", "");
		String committer = commit.getCommitter().getUsername();
		if (committer == null) {
			return MessageFormat.format("\\[[**{0}**]({1})\\] *Unrecognized author* pushed commit [**{2}**]({3}) to [**{4}**]({5})",
				pushEvent.getRepository().getFullName(), 
				pushEvent.getRepository().getHtmlUrl(),
				commit.getId().substring(0, 8), 
				commit.getUrl(),
				branch,
				pushEvent.getRepository().getUrl() + "/tree/" + branch);
		}
		else {
			return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) pushed commit [**{4}**]({5}) to [**{6}**]({7})",
				pushEvent.getRepository().getFullName(), 
				pushEvent.getRepository().getHtmlUrl(),
				committer, 
				"https://github.com/" + committer,
				commit.getId().substring(0, 8), 
				commit.getUrl(),
				branch,
				pushEvent.getRepository().getUrl() + "/tree/" + branch);
		}
	}

	public String stringify(PushEvent pushEvent, int size) {
		String commitText = (size == 1 ? "commit" : "commits");
		String branch = pushEvent.getRef().replace("refs/heads/", "");
		return MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) pushed {4} {5} to [**{6}**]({7})",
			pushEvent.getRepository().getFullName(), 
			pushEvent.getRepository().getHtmlUrl(),
			pushEvent.getPusher().getName(),
			"https://github.com/" + pushEvent.getPusher().getName(), 
			size,
			commitText,
			branch,
			pushEvent.getRepository().getUrl() + "/tree/" + branch);
	}
	

}
