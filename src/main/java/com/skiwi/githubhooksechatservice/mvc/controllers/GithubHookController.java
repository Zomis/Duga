
package com.skiwi.githubhooksechatservice.mvc.controllers;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.skiwi.githubhooksechatservice.github.events.CreateEvent;
import com.skiwi.githubhooksechatservice.github.events.DeleteEvent;
import com.skiwi.githubhooksechatservice.github.events.IssuesEvent;
import com.skiwi.githubhooksechatservice.github.events.PingEvent;
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
	
	@Autowired
	private ChatBot chatBot;
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=ping")
    @ResponseBody
    public void ping(final @RequestBody PingEvent pingEvent) {
        chatBot.postMessage("Ping: " + pingEvent.getZen());
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
				chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) opened issue [**#{4}: {5}**]({6})",
					issuesEvent.getRepository().getFullName(),
					issuesEvent.getRepository().getHtmlUrl(),
					issuesEvent.getSender().getLogin(),
					issuesEvent.getSender().getHtmlUrl(),
					issuesEvent.getIssue().getNumber(),
					issuesEvent.getIssue().getTitle(),
					issuesEvent.getIssue().getHtmlUrl()));
				chatBot.postMessage("> " + issuesEvent.getIssue().getBody());
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
	
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=push")
    @ResponseBody
    public void push(final @RequestBody PushEvent pushEvent) {
        pushEvent.getCommits().forEach(commit -> {
			String branch = pushEvent.getRef().replace("refs/heads/", "");
			String committer = commit.getCommitter().getUsername();
			chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) pushed commit [**{4}**]({5}) to [**{6}**]({7})",
				pushEvent.getRepository().getFullName(), 
				pushEvent.getRepository().getHtmlUrl(),
				committer, 
				"https://github.com/" + committer,
				commit.getId().substring(0, 8), 
				commit.getUrl(),
				branch,
				pushEvent.getRepository().getUrl() + "/tree/" + branch));
			chatBot.postMessage("> " + commit.getMessage());
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
}
