
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
import com.skiwi.githubhooksechatservice.github.events.Commit;
import com.skiwi.githubhooksechatservice.github.events.CommitCommentEvent;
import com.skiwi.githubhooksechatservice.github.events.CreateEvent;
import com.skiwi.githubhooksechatservice.github.events.DeleteEvent;
import com.skiwi.githubhooksechatservice.github.events.IssueCommentEvent;
import com.skiwi.githubhooksechatservice.github.events.IssuesEvent;
import com.skiwi.githubhooksechatservice.github.events.LegacyCommit;
import com.skiwi.githubhooksechatservice.github.events.PingEvent;
import com.skiwi.githubhooksechatservice.github.events.PullRequestEvent;
import com.skiwi.githubhooksechatservice.github.events.PushEvent;
import com.skiwi.githubhooksechatservice.github.events.WatchEvent;

/**
 *
 * @author Frank van Heeswijk
 */
@Controller
@RequestMapping("/hooks/github")
public class GithubHookController {
	private final static Logger LOGGER = Logger.getLogger(StackExchangeChatBot.class.getSimpleName());
	
	private final int MAX_NUMBER_NON_DISTINCT_COMMITS_PER_LINE = 2;
	
	@Autowired
	private ChatBot chatBot;
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=ping")
    @ResponseBody
    public void ping(final @RequestBody PingEvent pingEvent) {
        chatBot.postMessage("Ping: " + pingEvent.getZen());
    }
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=commit_comment")
    @ResponseBody
    public void commitComment(final @RequestBody CommitCommentEvent commitCommentEvent) {
		if (commitCommentEvent.getComment().getPath() == null) {
			chatBot.postMessages(
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
			chatBot.postMessages(
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
    
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=create")
    @ResponseBody
    public void create(final @RequestBody CreateEvent createEvent) {
		String refUrl = null;
		switch (createEvent.getRefType()) {
			case "branch":
				refUrl = createEvent.getRepository().getHtmlUrl() + "/tree/" + createEvent.getRef();
				break;
			case "tag":
				refUrl = createEvent.getRepository().getHtmlUrl() + "/releases/tag/" + createEvent.getRef();
				break;
		}
		chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created {4} [**{5}**]({6})",
			createEvent.getRepository().getFullName(),
			createEvent.getRepository().getHtmlUrl(),
			createEvent.getSender().getLogin(),
			createEvent.getSender().getHtmlUrl(),
			createEvent.getRefType(),
			createEvent.getRef(),
			refUrl));
    }
    
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=delete")
    @ResponseBody
    public void delete(final @RequestBody DeleteEvent deleteEvent) {
		chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) deleted {4} **{5}**",
			deleteEvent.getRepository().getFullName(),
			deleteEvent.getRepository().getHtmlUrl(),
			deleteEvent.getSender().getLogin(),
			deleteEvent.getSender().getHtmlUrl(),
			deleteEvent.getRefType(),
			deleteEvent.getRef()));
    }
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=issues")
    @ResponseBody
    public void issues(final @RequestBody IssuesEvent issuesEvent) {
		switch (issuesEvent.getAction()) {
			case "assigned":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) assigned [**{4}**]({5}) to issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getAssignee().getLogin(),
					issuesEvent.getAssignee().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "unassigned":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) unassigned [**{4}**]({5}) from issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getAssignee().getLogin(),
					issuesEvent.getAssignee().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "labeled":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added label [**{4}**]({5}) to issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getLabel().getName(),
					issuesEvent.getRepository().getHtmlUrl() + "/labels/" + issuesEvent.getLabel().getName().replace(" ", "%20"),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "unlabeled":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) removed label [**{4}**]({5}) from issue [**#{6}: {7}**]({8})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getLabel().getName(),
					issuesEvent.getRepository().getHtmlUrl() + "/labels/" + issuesEvent.getLabel().getName().replace(" ", "%20"),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "opened":
				if (issuesEvent.getIssue().getBody().isEmpty()) {
					chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) opened issue [**#{4}: {5}**]({6})",
							issuesEvent.getRepository().getFullName(),
							issuesEvent.getRepository().getHtmlUrl(),
							issuesEvent.getSender().getLogin(),
							issuesEvent.getSender().getHtmlUrl(),
							issuesEvent.getIssue().getNumber(),
							issuesEvent.getIssue().getTitle(),
							issuesEvent.getIssue().getHtmlUrl()));
				}
				else{
					chatBot.postMessages(
						MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) opened issue [**#{4}: {5}**]({6})",
							issuesEvent.getRepository().getFullName(),
							issuesEvent.getRepository().getHtmlUrl(),
							issuesEvent.getSender().getLogin(),
							issuesEvent.getSender().getHtmlUrl(),
							issuesEvent.getIssue().getNumber(),
							issuesEvent.getIssue().getTitle(),
							issuesEvent.getIssue().getHtmlUrl()),
						"> " + issuesEvent.getIssue().getBody());
				}
				break;
			case "closed":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) closed issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
			case "reopened":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) reopened issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle(),
					issuesEvent.getIssue().getHtmlUrl()));
				break;
		}
    }
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=issue_comment")
    @ResponseBody
    public void issueComment(final @RequestBody IssueCommentEvent issueCommentEvent) {
		switch (issueCommentEvent.getAction()) {
			case "created":
				chatBot.postMessages(
					MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) [commented]({4}) on issue [**#{5}: {6}**]({7})",
						issueCommentEvent.getRepository().getFullName(),
						issueCommentEvent.getRepository().getHtmlUrl(),
						issueCommentEvent.getSender().getLogin(),
						issueCommentEvent.getSender().getHtmlUrl(),
						issueCommentEvent.getComment().getHtmlUrl(),
						issueCommentEvent.getIssue().getNumber(),
						issueCommentEvent.getIssue().getTitle(),
						issueCommentEvent.getIssue().getHtmlUrl()),
					"> " + issueCommentEvent.getComment().getBody());
				break;
		}
    }
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=pull_request")
    @ResponseBody
    public void pullRequest(final @RequestBody PullRequestEvent pullRequestEvent) {
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
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) assigned [**{4}**]({5}) to pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getAssignee().getLogin(),
					pullRequestEvent.getAssignee().getHtmlUrl(),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle(),
					pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "unassigned":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) unassigned [**{4}**]({5}) from pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getAssignee().getLogin(),
					pullRequestEvent.getAssignee().getHtmlUrl(),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle(),
					pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "labeled":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) added label [**{4}**]({5}) to pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getLabel().getName(),
					pullRequestEvent.getRepository().getHtmlUrl() + "/labels/" + pullRequestEvent.getLabel().getName().replace(" ", "%20"),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle(),
					pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "unlabeled":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) removed label [**{4}**]({5}) from pull request [**#{6}: {7}**]({8})",
					pullRequestEvent.getRepository().getFullName(),
					pullRequestEvent.getRepository().getHtmlUrl(),
					pullRequestEvent.getSender().getLogin(),
					pullRequestEvent.getSender().getHtmlUrl(),
					pullRequestEvent.getLabel().getName(),
					pullRequestEvent.getRepository().getHtmlUrl() + "/labels/" + pullRequestEvent.getLabel().getName().replace(" ", "%20"),
					pullRequestEvent.getPullRequest().getNumber(),
					pullRequestEvent.getPullRequest().getTitle(),
					pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "opened":
				if (pullRequestEvent.getPullRequest().getBody().isEmpty()) {
					chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created pull request [**#{4}: {5}**]({6}) to merge [**{7}**]({8}) into [**{9}**]({10})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle(),
							pullRequestEvent.getPullRequest().getHtmlUrl(),
							headText,
							head.getRepo().getHtmlUrl() + "/tree/" + head.getRef(),
							baseText,
							base.getRepo().getHtmlUrl() + "/tree/" + base.getRef()));
				}
				else {
					chatBot.postMessages(
						MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created pull request [**#{4}: {5}**]({6}) to merge [**{7}**]({8}) into [**{9}**]({10})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle(),
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
					chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) merged pull request [**#{4}: {5}**]({6}) from [**{7}**]({8}) into [**{9}**]({10})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle(),
							pullRequestEvent.getPullRequest().getHtmlUrl(),
							headText,
							head.getRepo().getHtmlUrl() + "/tree/" + head.getRef(),
							baseText,
							base.getRepo().getHtmlUrl() + "/tree/" + base.getRef()));
				}
				else {
					chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) rejected pull request [**#{4}: {5}**]({6})",
							pullRequestEvent.getRepository().getFullName(),
							pullRequestEvent.getRepository().getHtmlUrl(),
							pullRequestEvent.getSender().getLogin(),
							pullRequestEvent.getSender().getHtmlUrl(),
							pullRequestEvent.getPullRequest().getNumber(),
							pullRequestEvent.getPullRequest().getTitle(),
							pullRequestEvent.getPullRequest().getHtmlUrl()));
				}
				break;
			case "reopened":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) reopened pull request [**#{4}: {5}**]({6})",
						pullRequestEvent.getRepository().getFullName(),
						pullRequestEvent.getRepository().getHtmlUrl(),
						pullRequestEvent.getSender().getLogin(),
						pullRequestEvent.getSender().getHtmlUrl(),
						pullRequestEvent.getPullRequest().getNumber(),
						pullRequestEvent.getPullRequest().getTitle(),
						pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
			case "synchronized":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) synchronized pull request [**#{4}: {5}**]({6})",
						pullRequestEvent.getRepository().getFullName(),
						pullRequestEvent.getRepository().getHtmlUrl(),
						pullRequestEvent.getSender().getLogin(),
						pullRequestEvent.getSender().getHtmlUrl(),
						pullRequestEvent.getPullRequest().getNumber(),
						pullRequestEvent.getPullRequest().getTitle(),
						pullRequestEvent.getPullRequest().getHtmlUrl()));
				break;
		}
    }
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=push")
    @ResponseBody
    public void push(final @RequestBody PushEvent pushEvent) {
		Map<Boolean, List<LegacyCommit>> partitionedCommits = pushEvent.getCommits().stream()
			.collect(Collectors.partitioningBy(LegacyCommit::isDistinct));

		List<LegacyCommit> distinctCommits = partitionedCommits.get(true);
		List<LegacyCommit> nonDistinctCommits = partitionedCommits.get(false);
		
		nonDistinctCommits.stream()
			.collect(Collectors.groupingBy(LegacyCommit::getCommitter))
			.forEach((committer, allCommits) -> {
				split(MAX_NUMBER_NON_DISTINCT_COMMITS_PER_LINE, allCommits).forEach(commits -> {
					String commitIds;
					String commitText;
					if (commits.size() == 1) {
						commitIds = MessageFormat.format("[**{0}**]({1})", commits.get(0).getId().substring(0, 8), commits.get(0).getUrl());
						commitText = "commit";
					}
					else {
						commitIds = commits.subList(0, commits.size() - 1).stream()
							.map(commit -> MessageFormat.format("[**{0}**]({1})", commit.getId().substring(0, 8), commit.getUrl()))
							.collect(Collectors.joining(", "));
						commitIds += " and " + MessageFormat.format("[**{0}**]({1})", commits.get(commits.size() - 1).getId().substring(0, 8), commits.get(commits.size() - 1).getUrl());
						commitText = "commits";
					}

					String branch = pushEvent.getRef().replace("refs/heads/", "");
					chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) pushed {4} {5} to [**{6}**]({7})",
						pushEvent.getRepository().getFullName(), 
						pushEvent.getRepository().getHtmlUrl(),
						committer.getUsername(), 
						"https://github.com/" + committer.getUsername(),
						commitText,
						commitIds,
						branch,
						pushEvent.getRepository().getUrl() + "/tree/" + branch));
				});
			});
		
        distinctCommits.forEach(commit -> {
			String branch = pushEvent.getRef().replace("refs/heads/", "");
			String committer = commit.getCommitter().getUsername();
			chatBot.postMessages(
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
		});
    }
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=watch")
    @ResponseBody
    public void watch(final @RequestBody WatchEvent watchEvent) {
		switch (watchEvent.getAction()) {
			case "started":
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) starred us",
					watchEvent.getRepository().getFullName(),
					watchEvent.getRepository().getHtmlUrl(),
					watchEvent.getSender().getLogin(),
					watchEvent.getSender().getHtmlUrl()));
				break;
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
