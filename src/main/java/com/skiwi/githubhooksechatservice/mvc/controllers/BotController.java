
package com.skiwi.githubhooksechatservice.mvc.controllers;

import com.skiwi.githubhooksechatservice.store.Store;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Frank van Heeswijk
 */
@Controller
@RequestMapping("/bot")
public class BotController {
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello() {
        System.out.println("test");
        return "Hello World!";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public void test() {
        Store.INSTANCE.getChatBot().postMessage("test");
    }

    @RequestMapping(value = "/say/{text}", method = RequestMethod.GET)
    @ResponseBody
    public void say(final @PathVariable("text") String text) {
        Store.INSTANCE.getChatBot().postMessage(text);
    }
}
