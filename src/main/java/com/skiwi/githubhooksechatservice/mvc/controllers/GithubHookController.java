
package com.skiwi.githubhooksechatservice.mvc.controllers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.chatbot.StackExchangeChatBot;
import com.skiwi.githubhooksechatservice.events.github.Commit;
import com.skiwi.githubhooksechatservice.events.github.CommitCommentEvent;
import com.skiwi.githubhooksechatservice.events.github.CreateEvent;
import com.skiwi.githubhooksechatservice.events.github.DeleteEvent;
import com.skiwi.githubhooksechatservice.events.github.ForkEvent;
import com.skiwi.githubhooksechatservice.events.github.GollumEvent;
import com.skiwi.githubhooksechatservice.events.github.IssueCommentEvent;
import com.skiwi.githubhooksechatservice.events.github.IssuesEvent;
import com.skiwi.githubhooksechatservice.events.github.LegacyCommit;
import com.skiwi.githubhooksechatservice.events.github.MemberEvent;
import com.skiwi.githubhooksechatservice.events.github.PingEvent;
import com.skiwi.githubhooksechatservice.events.github.PullRequestEvent;
import com.skiwi.githubhooksechatservice.events.github.PullRequestReviewCommentEvent;
import com.skiwi.githubhooksechatservice.events.github.PushEvent;
import com.skiwi.githubhooksechatservice.events.github.TeamAddEvent;
import com.skiwi.githubhooksechatservice.events.github.WatchEvent;
import com.skiwi.githubhooksechatservice.mvc.beans.Statistics;

/**
 *
 * @author Frank van Heeswijk
 */
@Controller
@RequestMapping({ "/hooks/github", "" })
public class GithubHookController {
	private final static Logger LOGGER = Logger.getLogger(StackExchangeChatBot.class.getSimpleName());
	
	private final static int MAX_NUMBER_NON_DISTINCT_COMMITS_PER_LINE = 2;
	
	@Autowired
	private ChatBot chatBot;
	
	@Autowired
	private Statistics statistics;
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=ping")
    @ResponseBody
    public void ping(final WebhookParameters params, final @RequestBody PingEvent pingEvent) {
        chatBot.postMessage(params, "Ping: " + pingEvent.getZen());
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=commit_comment")
    @ResponseBody
    public void commitComment(final WebhookParameters params, final @RequestBody CommitCommentEvent commitCommentEvent) {
		if (commitCommentEvent.getComment().getPath() == null) {
			chatBot.postMessages(params, 
				MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented]({4}) on commit [**{5}**]({6})",
					commitCommentEvent.getRepository().getFullName(),
					commitCommentEvent.getRepository().getHtmlUrl(),
					commitCommentEvent.getSender().getLogin(),
					commitCommentEvent.getSender().getHtmlUrl(),
					commitCommentEvent.getComment().getHtmlUrl(),
					commitCommentEvent.getComment().getCommitId().substring(0, 8),
					commitCommentEvent.getRepository().getHtmlUrl() + "/commit/" + commitCommentEvent.getComment().getCommitId()),
				"> " + commitCommentEvent.getComment().getBody());
		}
		else {
			chatBot.postMessages(params, 
				MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented on **{4}**]({5}) of commit [**{6}**]({7})",
					commitCommentEvent.getRepository().getFullName(),
					commitCommentEvent.getRepository().getHtmlUrl(),
					commitCommentEvent.getSender().getLogin(),
					commitCommentEvent.getSender().getHtmlUrl(),
					commitCommentEvent.getComment().getPath(),
					commitCommentEvent.getComment().getHtmlUrl(),
					commitCommentEvent.getComment().getCommitId().substring(0, 8),
					commitCommentEvent.getRepository().getHtmlUrl() + "/commit/" + commitCommentEvent.getComment().getCommitId()),
				"> " + commitCommentEvent.getComment().getBody());
		}
    }
    
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=create")
    @ResponseBody
    public void create(final WebhookParameters params, final @RequestBody CreateEvent createEvent) {
		String refUrl = null;
		switch (createEvent.getRefType()) {
			case "branch":
				refUrl = createEvent.getRepository().getHtmlUrl() + "/tree/" + createEvent.getRef();
				break;
			case "tag":
				refUrl = createEvent.getRepository().getHtmlUrl() + "/releases/tag/" + createEvent.getRef();
				break;
		}
		chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created {4} [**{5}**]({6})",
			createEvent.getRepository().getFullName(),
			createEvent.getRepository().getHtmlUrl(),
			createEvent.getSender().getLogin(),
			createEvent.getSender().getHtmlUrl(),
			createEvent.getRefType(),
			createEvent.getRef(),
			refUrl));
    }
    
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=delete")
    @ResponseBody
    public void delete(final WebhookParameters params, final @RequestBody DeleteEvent deleteEvent) {
		chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) deleted {4} **{5}**",
			deleteEvent.getRepository().getFullName(),
			deleteEvent.getRepository().getHtmlUrl(),
			deleteEvent.getSender().getLogin(),
			deleteEvent.getSender().getHtmlUrl(),
			deleteEvent.getRefType(),
			deleteEvent.getRef()));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=fork")
    @ResponseBody
    public void fork(final WebhookParameters params, final @RequestBody ForkEvent forkEvent) {
		chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) forked us into [**{4}**]({5})",
			forkEvent.getRepository().getFullName(),
			forkEvent.getRepository().getHtmlUrl(),
			forkEvent.getSender().getLogin(),
			forkEvent.getSender().getHtmlUrl(),
			forkEvent.getForkee().getFullName(),
			forkEvent.getForkee().getHtmlUrl()));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=gollum")
    @ResponseBody
    public void gollum(final WebhookParameters params, final @RequestBody GollumEvent gollumEvent) {
		gollumEvent.getPages().forEach(wikiPage -> {
			chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) {4} wiki page [**{5}**]({6})",
				gollumEvent.getRepository().getFullName(),
				gollumEvent.getRepository().getHtmlUrl(),
				gollumEvent.getSender().getLogin(),
				gollumEvent.getSender().getHtmlUrl(),
				wikiPage.getAction(),
				wikiPage.getTitle().trim(),
				wikiPage.getHtmlUrl()));
		});
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=issues")
    @ResponseBody
    public void issues(final WebhookParameters params, final @RequestBody IssuesEvent issuesEvent) {
		switch (issuesEvent.getAction()) {
			case "assigned":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) assigned [**{4}**]({5}) to issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getAssignee().getLogin(),
					issuesEvent.getAssignee().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "unassigned":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) unassigned [**{4}**]({5}) from issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getAssignee().getLogin(),
					issuesEvent.getAssignee().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "labeled":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added label [**{4}**]({5}) to issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getLabel().getName(),
					issuesEvent.getRepository().getHtmlUrl() + "/labels/" + issuesEvent.getLabel().getName().replace(" ", "%20"),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "unlabeled":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) removed label [**{4}**]({5}) from issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getLabel().getName(),
					issuesEvent.getRepository().getHtmlUrl() + "/labels/" + issuesEvent.getLabel().getName().replace(" ", "%20"),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "opened":
				if (issuesEvent.getIssue().getBody() == null || issuesEvent.getIssue().getBody().isEmpty()) {
					chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) opened issue [**#{4}: {5}**]({6})",
							issuesEvent.getRepository().getFullName(),
							issuesEvent.getRepository().getHtmlUrl(),
							issuesEvent.getSender().getLogin(),
							issuesEvent.getSender().getHtmlUrl(),
							issuesEvent.getIssue().getNumber(),
							issuesEvent.getIssue().getTitle().trim(),
							issuesEvent.getIssue().getHtmlUrl()));
				}
				else {
					chatBot.postMessages(params, 
						MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) opened issue [**#{4}: {5}**]({6})",
							issuesEvent.getRepository().getFullName(),
							issuesEvent.getRepository().getHtmlUrl(),
							issuesEvent.getSender().getLogin(),
							issuesEvent.getSender().getHtmlUrl(),
							issuesEvent.getIssue().getNumber(),
							issuesEvent.getIssue().getTitle().trim(),
							issuesEvent.getIssue().getHtmlUrl()),
						"> " + issuesEvent.getIssue().getBody());
				}
				statistics.add(issuesEvent, true);
				break;
			case "closed":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) closed issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl()));
				statistics.add(issuesEvent, false);
				break;
			case "reopened":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) reopened issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle().trim(),
					issuesEvent.getIssue().getHtmlUrl()));
				statistics.add(issuesEvent, true);
				break;
		}
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=issue_comment")
    @ResponseBody
    public void issueComment(final WebhookParameters params, final @RequestBody IssueCommentEvent issueCommentEvent) {
		switch (issueCommentEvent.getAction()) {
			case "created":
				String commentTarget = (issueCommentEvent.getIssue().getPullRequest() == null) ? "issue" : "pull request";
				chatBot.postMessages(params, 
					MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented]({4}) on {5} [**#{6}: {7}**]({8})",
						issueCommentEvent.getRepository().getFullName(),
						issueCommentEvent.getRepository().getHtmlUrl(),
						issueCommentEvent.getSender().getLogin(),
						issueCommentEvent.getSender().getHtmlUrl(),
						issueCommentEvent.getComment().getHtmlUrl(),
						commentTarget,
						issueCommentEvent.getIssue().getNumber(),
						issueCommentEvent.getIssue().getTitle().trim(),
						issueCommentEvent.getIssue().getHtmlUrl()),
					"> " + issueCommentEvent.getComment().getBody());
				break;
		}
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=member")
    @ResponseBody
    public void member(final WebhookParameters params, final @RequestBody MemberEvent memberEvent) {
		chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) {4} [**{5}**]({6})",
			memberEvent.getRepository().getFullName(),
			memberEvent.getRepository().getHtmlUrl(),
			memberEvent.getSender().getLogin(),
			memberEvent.getSender().getHtmlUrl(),
			memberEvent.getAction(),
			memberEvent.getMember().getLogin(),
			memberEvent.getMember().getHtmlUrl()));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=pull_request")
    @ResponseBody
    public void pullRequest(final WebhookParameters params, final @RequestBody PullRequestEvent pullRequestEvent) {
    	statistics.informAboutURL(pullRequestEvent.getRepository());
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
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) assigned [**{4}**]({5}) to pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getAssignee().getLogin(),
					pullRequestEvent.getAssignee().getHtmlUrl(),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle().trim(),
					pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "unassigned":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) unassigned [**{4}**]({5}) from pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getAssignee().getLogin(),
					pullRequestEvent.getAssignee().getHtmlUrl(),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle().trim(),
					pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "labeled":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added label [**{4}**]({5}) to pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getLabel().getName(),
					pullRequestEvent.getRepository().getHtmlUrl() + "/labels/" + pullRequestEvent.getLabel().getName().replace(" ", "%20"),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle().trim(),
					pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "unlabeled":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) removed label [**{4}**]({5}) from pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getLabel().getName(),
					pullRequestEvent.getRepository().getHtmlUrl() + "/labels/" + pullRequestEvent.getLabel().getName().replace(" ", "%20"),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle().trim(),
					pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "opened":
				if (pullRequestEvent.getPullRequest().getBody() == null || pullRequestEvent.getPullRequest().getBody().isEmpty()) {
					chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created pull request [**#{4}: {5}**]({6}) to merge [**{7}**]({8}) into [**{9}**]({10})",
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
							base.getRepo().getHtmlUrl() + "/tree/" + base.getRef()));
				}
				else {
					chatBot.postMessages(params, 
						MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created pull request [**#{4}: {5}**]({6}) to merge [**{7}**]({8}) into [**{9}**]({10})",
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
							base.getRepo().getHtmlUrl() + "/tree/" + base.getRef()),
						"> " + pullRequestEvent.getPullRequest().getBody());
				}
				break;
			case "closed":
				if (pullRequestEvent.getPullRequest().isMerged()) {
					chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) merged pull request [**#{4}: {5}**]({6}) from [**{7}**]({8}) into [**{9}**]({10})",
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
							base.getRepo().getHtmlUrl() + "/tree/" + base.getRef()));
				}
				else {
					chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) rejected pull request [**#{4}: {5}**]({6})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle().trim(),
							pullRequestEvent.getPullRequest().getHtmlUrl()));
				}
				break;
			case "reopened":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) reopened pull request [**#{4}: {5}**]({6})",
						pullRequestEvent.getRepository().getFullName(),
						pullRequestEvent.getRepository().getHtmlUrl(),
						pullRequestEvent.getSender().getLogin(),
						pullRequestEvent.getSender().getHtmlUrl(),
						pullRequestEvent.getPullRequest().getNumber(),
						pullRequestEvent.getPullRequest().getTitle().trim(),
						pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "synchronize":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) synchronized pull request [**#{4}: {5}**]({6})",
						pullRequestEvent.getRepository().getFullName(),
						pullRequestEvent.getRepository().getHtmlUrl(),
						pullRequestEvent.getSender().getLogin(),
						pullRequestEvent.getSender().getHtmlUrl(),
						pullRequestEvent.getPullRequest().getNumber(),
						pullRequestEvent.getPullRequest().getTitle().trim(),
						pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
		}
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=pull_request_review_comment")
    @ResponseBody
    public void pullRequestReviewComment(final WebhookParameters params, final @RequestBody PullRequestReviewCommentEvent pullRequestReviewCommentEvent) {
		switch (pullRequestReviewCommentEvent.getAction()) {
			case "created":
				chatBot.postMessages(params, 
					MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented on **{4}**]({5}) of pull request [**#{6}: {7}**]({8})",
						pullRequestReviewCommentEvent.getRepository().getFullName(),
						pullRequestReviewCommentEvent.getRepository().getHtmlUrl(),
						pullRequestReviewCommentEvent.getSender().getLogin(),
						pullRequestReviewCommentEvent.getSender().getHtmlUrl(),
						pullRequestReviewCommentEvent.getComment().getPath(),
						pullRequestReviewCommentEvent.getComment().getHtmlUrl(),
						pullRequestReviewCommentEvent.getPullRequest().getNumber(),
						pullRequestReviewCommentEvent.getPullRequest().getTitle().trim(),
						pullRequestReviewCommentEvent.getPullRequest().getHtmlUrl()),
					"> " + pullRequestReviewCommentEvent.getComment().getBody());
					break;
		}
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=push")
    @ResponseBody
    public void push(final WebhookParameters params, final @RequestBody PushEvent pushEvent) {
		Map<Boolean, List<LegacyCommit>> partitionedCommits = pushEvent.getCommits().stream()
			.collect(Collectors.partitioningBy(LegacyCommit::isDistinct));

		List<LegacyCommit> distinctCommits = partitionedCommits.get(true);
		List<LegacyCommit> nonDistinctCommits = partitionedCommits.get(false);
		
		if (!nonDistinctCommits.isEmpty()) {
			String commitText = (nonDistinctCommits.size() == 1) ? "commit" : "commits";
			String branch = pushEvent.getRef().replace("refs/heads/", "");
			chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) pushed {4} {5} to [**{6}**]({7})",
				pushEvent.getRepository().getFullName(), 
				pushEvent.getRepository().getHtmlUrl(),
				pushEvent.getPusher().getName(),
				"https://github.com/" + pushEvent.getPusher().getName(), 
				nonDistinctCommits.size(),
				commitText,
				branch,
				pushEvent.getRepository().getUrl() + "/tree/" + branch));
		}
		
        distinctCommits.forEach(commit -> {
			String branch = pushEvent.getRef().replace("refs/heads/", "");
			String committer = commit.getCommitter().getUsername();
			if (committer == null) {
				chatBot.postMessages(params, 
					MessageFormat.format("\\[[**{0}**]({1})\\] *Unrecognized author* pushed commit [**{2}**]({3}) to [**{4}**]({5})",
						pushEvent.getRepository().getFullName(), 
						pushEvent.getRepository().getHtmlUrl(),
						commit.getId().substring(0, 8), 
						commit.getUrl(),
						branch,
						pushEvent.getRepository().getUrl() + "/tree/" + branch),
					"> " + commit.getMessage());
			}
			else {
				chatBot.postMessages(params, 
					MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) pushed commit [**{4}**]({5}) to [**{6}**]({7})",
						pushEvent.getRepository().getFullName(), 
						pushEvent.getRepository().getHtmlUrl(),
						committer, 
						"https://github.com/" + committer,
						commit.getId().substring(0, 8), 
						commit.getUrl(),
						branch,
						pushEvent.getRepository().getUrl() + "/tree/" + branch),
					"> " + commit.getMessage());
			}
			statistics.add(pushEvent.getRepository(), commit);
		});
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=watch")
    @ResponseBody
    public void watch(final WebhookParameters params, final @RequestBody WatchEvent watchEvent) {
		switch (watchEvent.getAction()) {
			case "started":
				chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) starred us",
					watchEvent.getRepository().getFullName(),
					watchEvent.getRepository().getHtmlUrl(),
					watchEvent.getSender().getLogin(),
					watchEvent.getSender().getHtmlUrl()));
				break;
		}
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=team_add")
    @ResponseBody
    public void teamAdd(final WebhookParameters params, final @RequestBody TeamAddEvent teamAddEvent) {
		if (teamAddEvent.getUser() == null) {
			chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added us to team [**{4}**]({5})",
				teamAddEvent.getRepository().getFullName(),
				teamAddEvent.getRepository().getHtmlUrl(),
				teamAddEvent.getSender().getLogin(),
				teamAddEvent.getSender().getHtmlUrl(),
				teamAddEvent.getTeam().getName(),
				teamAddEvent.getSender().getHtmlUrl() + "/" + teamAddEvent.getTeam().getName()));
		}
		else {
			chatBot.postMessage(params, MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added [**{4}**]({5}) to team [**{6}**]({7})",
				teamAddEvent.getRepository().getFullName(),
				teamAddEvent.getRepository().getHtmlUrl(),
				teamAddEvent.getSender().getLogin(),
				teamAddEvent.getSender().getHtmlUrl(),
				teamAddEvent.getUser().getLogin(),
				teamAddEvent.getUser().getHtmlUrl(),
				teamAddEvent.getTeam().getName(),
				teamAddEvent.getSender().getHtmlUrl() + "/" + teamAddEvent.getTeam().getName()));
		}
    }
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleException(final Exception ex, final HttpServletRequest request) {
		LOGGER.log(Level.SEVERE, "exception", ex);
	}
	
	private static <T> List<List<T>> split(int size, List<T> data) {
		List<List<T>> returnList = new ArrayList<>((data.size() / size) + 1);
		List<T> current = new ArrayList<>(size);
		returnList.add(current);
		for (T d : data) {
			if (current.size() == size) {
				current = new ArrayList<>(size);
				returnList.add(current);
			}
			current.add(d);
		}
		return returnList;
	}
}
