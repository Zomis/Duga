package com.skiwi.githubhooksechatservice.mvc.controllers;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.events.github.AbstractEvent;
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
	private GithubBean githubUtils;
	
	@Autowired
	private GithubService githubService;
	
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String hello() {
        System.out.println("test");
        return "Hello World!";
    }

    @RequestMapping(value = "/gittest", method = RequestMethod.GET)
    @ResponseBody
    public String gitScan(@RequestParam("name") String name, @RequestParam(required = false, value = "user") Boolean user) {
    	AbstractEvent[] blocks = githubUtils.fetchEvents(false, name);
        return Arrays.toString(blocks);
    }

    @RequestMapping(value = "/gittrack", method = RequestMethod.GET)
    @ResponseBody
    public String gitTrack(@RequestParam("name") String name) {
    	AbstractEvent[] blocks = githubUtils.fetchEvents(false, name);
    	long eventId = Arrays.stream(blocks).mapToLong(bl -> bl.getId()).max().orElse(0);
    	githubService.update(name, Instant.now().getEpochSecond(), eventId, false);
        return Arrays.toString(blocks);
    }

    @RequestMapping(value = "/gituser", method = RequestMethod.GET)
    @ResponseBody
    public String gitTrackUser(@RequestParam("name") String name) {
    	AbstractEvent[] blocks = githubUtils.fetchEvents(true, name);
    	long eventId = Arrays.stream(blocks).mapToLong(bl -> bl.getId()).max().orElse(0);
    	githubService.update(name, Instant.now().getEpochSecond(), eventId, true);
        return Arrays.toString(blocks);
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
