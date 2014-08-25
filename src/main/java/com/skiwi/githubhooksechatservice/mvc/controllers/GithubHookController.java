
package com.skiwi.githubhooksechatservice.mvc.controllers;

import com.skiwi.githubhooksechatservice.store.Store;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Frank van Heeswijk
 */
@Controller
@RequestMapping("/hooks/github")
public class GithubHookController {
    @RequestMapping(value = "/payload", method = RequestMethod.POST, headers = "X-Github-Event=push")
    public void push(final String jsonString) {
        System.out.println("test push: " + jsonString);
        Store.INSTANCE.getChatBot().postMessage("test push: " + jsonString);
    }
}
