
package com.skiwi.githubhooksechatservice.mvc.controllers;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.events.travis.BuildEvent;

/**
 *
 * @author Frank van Heeswijk
 */
@Controller
@RequestMapping("/hooks/travis")
public class TravisHookController {
	private final static Logger LOGGER = Logger.getLogger(TravisHookController.class.getSimpleName());
	
	@Autowired
	private ChatBot chatBot;
	
	@RequestMapping(value = "/payload", method = RequestMethod.POST)
	@ResponseBody
	public void build(final @RequestParam("payload") String buildEventJson) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		BuildEvent buildEvent = objectMapper.readValue(buildEventJson, BuildEvent.class);
		chatBot.postMessage(MessageFormat.format("\\[[**{0}**]({1})\\] [**build #{2}**]({3}) for commit [**{4}**]({5}) on branch [**{6}**]({7}) {8}",
			buildEvent.getRepository().getOwnerName() + "/" + buildEvent.getRepository().getName(),
			buildEvent.getRepository().getUrl(),
			buildEvent.getNumber(),
			buildEvent.getBuildUrl(),
			buildEvent.getCommit().substring(0, 8),
			buildEvent.getRepository().getUrl() + "/commit/" + buildEvent.getCommit(),
			buildEvent.getBranch(),
			buildEvent.getRepository().getUrl() + "/tree/" + buildEvent.getBranch(),
			buildEvent.getStatusMessage().toLowerCase(Locale.ENGLISH)));
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleException(final Exception ex, final HttpServletRequest request) {
		LOGGER.log(Level.SEVERE, "exception", ex);
	}
}
