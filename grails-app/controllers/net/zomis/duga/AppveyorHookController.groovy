package net.zomis.duga

import net.zomis.duga.chat.BotRoom
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author Simon Forsberg
 */
class AppveyorHookController {
    static allowedMethods = [build:'POST']

	private final static Logger LOGGER = Logger.getLogger(AppveyorHookController.class.getSimpleName());
	
	@Autowired
	DugaBotService chatBot;
	
	def build() {
        println 'AppVeyor!'
        println 'JSON Data: ' + params
        println 'Request: ' + request
        JSONObject buildEvent = request.JSON
        println 'JSON Request: ' + buildEvent
        String room = params?.roomId
        println 'Room: ' + room
        BotRoom params = chatBot.room(room)
        String eventName = buildEvent.eventName.replace('_', ' ')
        def event = buildEvent.eventData

        String repoURL = "http://github.com/$event.repositoryName"

        String mess = "\\[[**$event.repositoryName**]($repoURL)\\] " +
                "[**build #$event.buildNumber**]($event.buildUrl) for commit " +
                "[**$event.commitId**]($repoURL/commit/$event.commitId) " +
                "@ [**$event.buildVersion**]($repoURL/tree/$event.branch) " +
                "$eventName"
        chatBot.postSingle(params, mess)
        if (eventName == 'build_failure') {
            chatBot.postSingle(params, '**BUILD FAILURE!**')
        }
        render 'OK'
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleException(final Exception ex, final HttpServletRequest request) {
		LOGGER.log(Level.SEVERE, "exception", ex);
	}
}
