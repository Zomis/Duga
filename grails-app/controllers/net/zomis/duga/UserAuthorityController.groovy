package net.zomis.duga

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class UserAuthorityController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond UserAuthority.list(params), model:[userAuthorityCount: UserAuthority.count()]
    }

    def show(UserAuthority userAuthority) {
        respond userAuthority
    }

    def create() {
        respond new UserAuthority(params)
    }

    @Transactional
    def save(UserAuthority userAuthority) {
        if (userAuthority == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userAuthority.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userAuthority.errors, view:'create'
            return
        }

        userAuthority.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userAuthority.label', default: 'UserAuthority'), userAuthority.id])
                redirect userAuthority
            }
            '*' { respond userAuthority, [status: CREATED] }
        }
    }

    def edit(UserAuthority userAuthority) {
        respond userAuthority
    }

    @Transactional
    def update(UserAuthority userAuthority) {
        if (userAuthority == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (userAuthority.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond userAuthority.errors, view:'edit'
            return
        }

        userAuthority.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'userAuthority.label', default: 'UserAuthority'), userAuthority.id])
                redirect userAuthority
            }
            '*'{ respond userAuthority, [status: OK] }
        }
    }

    @Transactional
    def delete(UserAuthority userAuthority) {

        if (userAuthority == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        userAuthority.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'userAuthority.label', default: 'UserAuthority'), userAuthority.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userAuthority.label', default: 'UserAuthority'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
