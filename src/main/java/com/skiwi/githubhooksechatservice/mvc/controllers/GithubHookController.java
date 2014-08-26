
package com.skiwi.githubhooksechatservice.mvc.controllers;

import com.skiwi.githubhooksechatservice.github.events.CreateEvent;
import com.skiwi.githubhooksechatservice.github.events.DeleteEvent;
import com.skiwi.githubhooksechatservice.github.events.PingEvent;
import com.skiwi.githubhooksechatservice.github.events.PushEvent;
import com.skiwi.githubhooksechatservice.store.Store;

import java.text.MessageFormat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Frank van Heeswijk
 */
@Controller
@RequestMapping("/hooks/github")
public class GithubHookController {
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=ping")
    @ResponseBody
    public void ping(final @RequestBody PingEvent pingEvent) {
        Store.INSTANCE.getChatBot().postMessage("Ping: " + pingEvent.getZen());
    }
    
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=push")
    @ResponseBody
    public void push(final @RequestBody PushEvent pushEvent) {
        pushEvent.getCommits().forEach(commit -> {
			String branch = pushEvent.getRef().replace("refs/heads/", "");
			String committer = commit.getCommitter().getAccountName();
			Store.INSTANCE.getChatBot().postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) pushed commit [**{4}**]({5}) to [**{6}**]({7})",
				pushEvent.getRepository().getFullName(), 
				pushEvent.getRepository().getHtmlUrl(),
				committer, 
				"https://github.com/" + committer,
				commit.getId().substring(0, 8), 
				commit.getUrl(),
				branch,
				pushEvent.getRepository().getUrl() + "/tree/" + branch));
			Store.INSTANCE.getChatBot().postMessage("> " + commit.getMessage());
		});
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
		Store.INSTANCE.getChatBot().postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) created {4} [**{5}**]({6})",
			createEvent.getRepository().getFullName(),
			createEvent.getRepository().getHtmlUrl(),
			createEvent.getSender().getAccountName(),
			createEvent.getSender().getHtmlUrl(),
			createEvent.getRefType(),
			createEvent.getRef(),
			refUrl));
    }
    
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=delete")
    @ResponseBody
    public void delete(final @RequestBody DeleteEvent deleteEvent) {
		Store.INSTANCE.getChatBot().postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**{2}**]({3}) deleted {4} **{5}**",
			deleteEvent.getRepository().getFullName(),
			deleteEvent.getRepository().getHtmlUrl(),
			deleteEvent.getSender().getAccountName(),
			deleteEvent.getSender().getHtmlUrl(),
			deleteEvent.getRefType(),
			deleteEvent.getRef()));
    }
}
