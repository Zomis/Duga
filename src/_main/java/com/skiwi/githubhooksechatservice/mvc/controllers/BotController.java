package com.skiwi.githubhooksechatservice.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.mvc.beans.GithubBean;
import com.skiwi.githubhooksechatservice.service.GithubService;

/**
 *
 * @author Frank van Heeswijk
 */
@Controller
@RequestMapping("/bot")
public class BotController {
	@Autowired
	private ChatBot chatBot;
	
	@Autowired
	private GithubBean githubBean;
	
	@Autowired
	private GithubService githubService;
	
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello() {
        System.out.println("test");
        return "Hello World!";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public void test(WebhookParameters params) {
        chatBot.postMessage(params, "test");
    }

    @RequestMapping(value = "/say/{text}", method = RequestMethod.GET)
    @ResponseBody
    public void say(WebhookParameters params, final @PathVariable("text") String text) {
        chatBot.postMessage(params, text);
    }
}
