package com.skiwi.githubhooksechatservice.mvc.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
import com.skiwi.githubhooksechatservice.events.github.classes.LegacyCommit;
import com.skiwi.githubhooksechatservice.events.github.classes.PingEvent;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubBean;
import com.skiwi.githubhooksechatservice.mvc.beans.Statistics;
import com.skiwi.githubhooksechatservice.service.RuntimeLogService;

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
	
	@Autowired
	private GithubBean githubBean;
	
	private RuntimeLogService runtimeLog;
	
	private final Map<Class<? extends AbstractEvent>, BiConsumer<WebhookParameters, AbstractEvent>> map;
	
	public GithubHookController() {
		this.map = new HashMap<>();
		this.map.put(PingEvent.class, (params, event) -> ping(params, (PingEvent) event));
		this.map.put(CommitCommentEvent.class, (params, event) -> commitComment(params, (CommitCommentEvent) event));
		this.map.put(CreateEvent.class, (params, event) -> create(params, (CreateEvent) event));
		this.map.put(DeleteEvent.class, (params, event) -> delete(params, (DeleteEvent) event));
		this.map.put(ForkEvent.class, (params, event) -> fork(params, (ForkEvent) event));
		this.map.put(GollumEvent.class, (params, event) -> gollum(params, (GollumEvent) event));
		this.map.put(IssuesEvent.class, (params, event) -> issues(params, (IssuesEvent) event));
		this.map.put(IssueCommentEvent.class, (params, event) -> issueComment(params, (IssueCommentEvent) event));
		this.map.put(MemberEvent.class, (params, event) -> member(params, (MemberEvent) event));
		this.map.put(PullRequestEvent.class, (params, event) -> pullRequest(params, (PullRequestEvent) event));
		this.map.put(PullRequestReviewCommentEvent.class, (params, event) -> pullRequestReviewComment(params, (PullRequestReviewCommentEvent) event));
		this.map.put(PushEvent.class, (params, event) -> push(params, (PushEvent) event));
		this.map.put(WatchEvent.class, (params, event) -> watch(params, (WatchEvent) event));
		this.map.put(TeamAddEvent.class, (params, event) -> teamAdd(params, (TeamAddEvent) event));
	}
	
	public void post(WebhookParameters params, AbstractEvent event) {
		BiConsumer<WebhookParameters, AbstractEvent> consumer = this.map.get(event.getClass());
		if (consumer != null) {
			consumer.accept(params, event);
		}
		else {
			runtimeLog.log("Unknown event class: " + event.getClass());
		}
	}
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=ping")
    @ResponseBody
    public void ping(final WebhookParameters params, final @RequestBody PingEvent pingEvent) {
        chatBot.postMessage(params, githubBean.stringify(pingEvent));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=commit_comment")
    @ResponseBody
    public void commitComment(final WebhookParameters params, final @RequestBody CommitCommentEvent commitCommentEvent) {
		chatBot.postMessages(params, githubBean.stringify(commitCommentEvent),
				"> " + commitCommentEvent.getComment().getBody());
    }
    
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=create")
    @ResponseBody
    public void create(final WebhookParameters params, final @RequestBody CreateEvent createEvent) {
		chatBot.postMessage(params, githubBean.stringify(createEvent));
    }
    
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=delete")
    @ResponseBody
    public void delete(final WebhookParameters params, final @RequestBody DeleteEvent deleteEvent) {
		chatBot.postMessage(params, githubBean.stringify(deleteEvent));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=fork")
    @ResponseBody
    public void fork(final WebhookParameters params, final @RequestBody ForkEvent forkEvent) {
		chatBot.postMessage(params, githubBean.stringify(forkEvent));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=gollum")
    @ResponseBody
    public void gollum(final WebhookParameters params, final @RequestBody GollumEvent gollumEvent) {
		gollumEvent.getPages().forEach(wikiPage -> chatBot.postMessage(params, githubBean.stringify(gollumEvent, wikiPage)));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=issues")
    @ResponseBody
    public void issues(final WebhookParameters params, final @RequestBody IssuesEvent issuesEvent) {
    	if (issuesEvent.getAction().equals("opened") && issuesEvent.getIssue().getBody() != null
    			&& !issuesEvent.getIssue().getBody().isEmpty()) {
        	chatBot.postMessages(params, githubBean.stringify(issuesEvent), "> " + issuesEvent.getIssue().getBody());
    	}
    	else {
        	chatBot.postMessages(params, githubBean.stringify(issuesEvent));
    	}
    	if (issuesEvent.isOpened()) {
        	statistics.add(issuesEvent, true);
    	}
    	if (issuesEvent.isClosed()) {
        	statistics.add(issuesEvent, false);
    	}
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=issue_comment")
    @ResponseBody
    public void issueComment(final WebhookParameters params, final @RequestBody IssueCommentEvent issueCommentEvent) {
		switch (issueCommentEvent.getAction()) {
			case "created":
				chatBot.postMessages(params, githubBean.stringify(issueCommentEvent),
					"> " + issueCommentEvent.getComment().getBody());
				statistics.add(issueCommentEvent);
				break;
		}
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=member")
    @ResponseBody
    public void member(final WebhookParameters params, final @RequestBody MemberEvent memberEvent) {
		chatBot.postMessage(params, githubBean.stringify(memberEvent));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=pull_request")
    @ResponseBody
    public void pullRequest(final WebhookParameters params, final @RequestBody PullRequestEvent pullRequestEvent) {
    	statistics.informAboutURL(pullRequestEvent.getRepository());
    	chatBot.postMessage(params, githubBean.stringify(pullRequestEvent));
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=pull_request_review_comment")
    @ResponseBody
    public void pullRequestReviewComment(final WebhookParameters params, final @RequestBody PullRequestReviewCommentEvent pullRequestReviewCommentEvent) {
		switch (pullRequestReviewCommentEvent.getAction()) {
			case "created":
				chatBot.postMessages(params, githubBean.stringify(pullRequestReviewCommentEvent),
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
			chatBot.postMessage(params, githubBean.stringify(pushEvent, nonDistinctCommits.size()));
		}
		
        distinctCommits.forEach(commit -> {
			chatBot.postMessages(params, githubBean.stringify(pushEvent, commit), "> " + commit.getMessage());
			statistics.add(pushEvent.getRepository(), commit);
		});
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=watch")
    @ResponseBody
    public void watch(final WebhookParameters params, final @RequestBody WatchEvent watchEvent) {
		switch (watchEvent.getAction()) {
			case "started":
				chatBot.postMessage(params, githubBean.stringify(watchEvent));
				break;
		}
    }
	
    @RequestMapping(value = { "/payload", "/hook" }, method = RequestMethod.POST, headers = "X-Github-Event=team_add")
    @ResponseBody
    public void teamAdd(final WebhookParameters params, final @RequestBody TeamAddEvent teamAddEvent) {
    	chatBot.postMessage(params, githubBean.stringify(teamAddEvent));
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
