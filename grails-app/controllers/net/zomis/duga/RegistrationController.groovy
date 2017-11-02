package net.zomis.duga

import grails.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class)

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        render(view: 'index')
    }

    def saved() {
        String key = params.result
        logger.info('Saved, responsekey: ' + key)
        render(view: 'saved', model: [key: key])
    }

    @Transactional
    def save() {
        String apiKey = params.apikey
        String responseKey = apiKey + Math.random()
        responseKey = responseKey.encodeAsMD5()
        User user = new User()
        user.apiKey = apiKey
        user.pingExpect = responseKey
        user.chatName = ''
        user.githubName = user.github('user').login
        user.username = user.githubName
        user.password = apiKey
        user.accountExpired = false
        user.accountLocked = true
        user.credentialsExpired = false
        user.enabled = true

        if (user.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond user.errors, view:'signup'
            return
        }

        user.save flush:true, failOnError: true
        UserAuthority.create(user, Authority.findByAuthority('ROLE_USER'), true)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])
                redirect action: "saved", params: [result: responseKey]
            }
            '*' { respond user, [status: CREATED] }
        }
    }

}
