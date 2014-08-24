
package com.skiwi.githubhooksechatservice.mvc.controllers;

import com.skiwi.githubhooksechatservice.github.events.PingEvent;
import com.skiwi.githubhooksechatservice.github.events.PushEvent;
import com.skiwi.githubhooksechatservice.store.Store;
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
            Store.INSTANCE.getChatBot().postMessage("[" + pushEvent.getRepository().getFullName() + "] " + commit.getCommitter().getUsername() + " pushed commit " + commit.getId().substring(0, 10) + " to " + pushEvent.getRef().replace("refs/heads/", "") + System.lineSeparator() + commit.getMessage());
        });
    }
}
