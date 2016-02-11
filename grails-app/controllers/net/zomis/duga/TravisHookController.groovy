package net.zomis.duga

import net.zomis.duga.chat.WebhookParameters
import org.grails.web.json.JSONObject;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Frank van Heeswijk
 * @author Simon Forsberg
 */
class TravisHookController {
    static allowedMethods = [build:'POST']
	private final static Logger LOGGER = Logger.getLogger(TravisHookController.class.getSimpleName());
	
	@Autowired
	private DugaBotService chatBot;
	
    @Autowired
    private GithubBean githubBean
	
	@RequestMapping(value = "/payload", method = RequestMethod.POST)
	@ResponseBody
	public void build() {
        String room = params?.roomId
        JSONObject buildEvent = request.JSON
        WebhookParameters params = WebhookParameters.toRoom(room)
// TODO:		stats.fixRepositoryURL(buildEvent.getRepository());
		List<String> messages = new ArrayList<>();
		
		switch (buildEvent.type) {
			case "push":
				messages.add(MessageFormat.format("\\[[**{0}**]({1})\\] [**build #{2}**]({3}) for commit [**{4}**]({5}) on branch [**{6}**]({7}) {8}",
					buildEvent.repository.owner_name + "/" + buildEvent.repository.name,
					buildEvent.repository.url,
					buildEvent.number,
					buildEvent.build_url,
					buildEvent.commit.substring(0, 8),
					buildEvent.repository.url + "/commit/" + buildEvent.commit,
					buildEvent.branch,
					buildEvent.repository.url + "/tree/" + buildEvent.branch,
					buildEvent.status_message.toLowerCase(Locale.ENGLISH)));
				break;
			case "pull_request":
				messages.add(MessageFormat.format("\\[[**{0}**]({1})\\] [**build #{2}**]({3}) for commit [**{4}**]({5}) from pull request [**#{6}**]({7}) to branch [**{8}**]({9}) {10}",
                    buildEvent.repository.owner_name + "/" + buildEvent.repository.name,
                    buildEvent.repository.url,
                    buildEvent.number,
                    buildEvent.build_url,
                    buildEvent.commit.substring(0, 8),
                    buildEvent.repository.url + "/commit/" + buildEvent.commit,
                    buildEvent.pull_request_number,
                    buildEvent.repository.url + "/pull/" + buildEvent.pull_request_number,
                    buildEvent.branch,
                    buildEvent.repository.url + "/tree/" + buildEvent.branch,
					buildEvent.status_message.toLowerCase(Locale.ENGLISH)));
				break;
			default:
				break;
		}
		
		switch (buildEvent.status_message.toLowerCase(Locale.ENGLISH)) {
			case "broken":
            case "failed":
			case "still failing":
			case "errored":
                String commitApiUrl = "repos/" + buildEvent.repository.owner_name + "/" + buildEvent.repository.name + "/commits/" + buildEvent.commit;
                def commitResponse = githubBean.githubAPI(commitApiUrl)
                if (commitResponse) {
                    String githubCommitter = commitResponse.committer.login;
                    String githubAuthor = commitResponse.author.login;
                    String sechatCommitter = githubCommitter; // configuration.getUserMappingsMap().getOrDefault(githubCommitter, githubCommitter);
                    String sechatAuthor = githubAuthor; // configuration.getUserMappingsMap().getOrDefault(githubAuthor, githubAuthor);
                    // TODO: Lookup the usernames in the database, as it contains both github user name and SE-chat user name

                    String mentionedUsers = Stream.of(sechatCommitter, sechatAuthor)
                            .distinct()
                            .map({user -> "@" + user})
                            .collect(Collectors.joining(", "));
                    messages.add("**$mentionedUsers, your build reported bad status: $buildEvent.status_message!**");
                }
				break;
			default:
				break;
		}
		chatBot.postChat(params, messages);
        render 'OK'
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleException(final Exception ex, final HttpServletRequest request) {
		LOGGER.log(Level.SEVERE, "exception", ex);
	}
}
