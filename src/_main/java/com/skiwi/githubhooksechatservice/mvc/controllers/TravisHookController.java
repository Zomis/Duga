
package com.skiwi.githubhooksechatservice.mvc.controllers;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.skiwi.githubhooksechatservice.apis.commit.CommitResponse;
import com.skiwi.githubhooksechatservice.chatbot.ChatBot;
import com.skiwi.githubhooksechatservice.events.travis.BuildEvent;
import com.skiwi.githubhooksechatservice.mvc.beans.Statistics;
import com.skiwi.githubhooksechatservice.mvc.configuration.BotConfiguration;

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
	
	@Autowired
	private BotConfiguration configuration;
	
	@Autowired
	private Statistics stats;
	
	@RequestMapping(value = "/payload", method = RequestMethod.POST)
	@ResponseBody
	public void build(final WebhookParameters params, final @RequestParam("payload") String buildEventJson) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		BuildEvent buildEvent = objectMapper.readValue(buildEventJson, BuildEvent.class);
		stats.fixRepositoryURL(buildEvent.getRepository());
		List<String> messages = new ArrayList<>();
		
		switch (buildEvent.getType()) {
			case "push":
				messages.add(MessageFormat.format("\\[[**{0}**]({1})\\] [**build #{2}**]({3}) for commit [**{4}**]({5}) on branch [**{6}**]({7}) {8}",
					buildEvent.getRepository().getOwnerName() + "/" + buildEvent.getRepository().getName(),
					buildEvent.getRepository().getUrl(),
					buildEvent.getNumber(),
					buildEvent.getBuildUrl(),
					buildEvent.getCommit().substring(0, 8),
					buildEvent.getRepository().getUrl() + "/commit/" + buildEvent.getCommit(),
					buildEvent.getBranch(),
					buildEvent.getRepository().getUrl() + "/tree/" + buildEvent.getBranch(),
					buildEvent.getStatusMessage().toLowerCase(Locale.ENGLISH)));
				break;
			case "pull_request":
				messages.add(MessageFormat.format("\\[[**{0}**]({1})\\] [**build #{2}**]({3}) for commit [**{4}**]({5}) from pull request [**#{6}**]({7}) to branch [**{8}**]({9}) {10}",
					buildEvent.getRepository().getOwnerName() + "/" + buildEvent.getRepository().getName(),
					buildEvent.getRepository().getUrl(),
					buildEvent.getNumber(),
					buildEvent.getBuildUrl(),
					buildEvent.getCommit().substring(0, 8),
					buildEvent.getRepository().getUrl() + "/commit/" + buildEvent.getCommit(),
					buildEvent.getPullRequestNumber(),
					buildEvent.getRepository().getUrl() + "/pull/" + buildEvent.getPullRequestNumber(),
					buildEvent.getBranch(),
					buildEvent.getRepository().getUrl() + "/tree/" + buildEvent.getBranch(),
					buildEvent.getStatusMessage().toLowerCase(Locale.ENGLISH)));
				break;
			default:
				break;
		}
		
		switch (buildEvent.getStatusMessage().toLowerCase(Locale.ENGLISH)) {
			case "broken":
            case "failed":
			case "still failing":
			case "errored":
				String commitApiUrl = "https://api.github.com/repos/" + buildEvent.getRepository().getOwnerName() + "/" + buildEvent.getRepository().getName() + "/commits/" + buildEvent.getCommit();
				CommitResponse commitResponse = objectMapper.readValue(new URL(commitApiUrl), CommitResponse.class);
				
				String githubCommitter = commitResponse.getCommitter().getLogin();
				String githubAuthor = commitResponse.getAuthor().getLogin();
				String sechatCommitter = configuration.getUserMappingsMap().getOrDefault(githubCommitter, githubCommitter);
				String sechatAuthor = configuration.getUserMappingsMap().getOrDefault(githubAuthor, githubAuthor);
				
				String mentionedUsers = Stream.of(sechatCommitter, sechatAuthor).distinct().map(user -> "@" + user).collect(Collectors.joining(", "));
				messages.add(MessageFormat.format("**{0}, your build reported bad status: {1}!**", 
					mentionedUsers, 
					buildEvent.getStatusMessage()));
				break;
			default:
				break;
		}
		
		chatBot.postMessages(params, messages);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleException(final Exception ex, final HttpServletRequest request) {
		LOGGER.log(Level.SEVERE, "exception", ex);
	}
}
