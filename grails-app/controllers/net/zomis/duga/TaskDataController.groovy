package net.zomis.duga

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class TaskDataController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond TaskData.list(params), model:[taskDataCount: TaskData.count()]
    }

    def show(TaskData taskData) {
        respond taskData
    }

    def create() {
        respond new TaskData(params)
    }

    @Transactional
    def save(TaskData taskData) {
        if (taskData == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (taskData.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond taskData.errors, view:'create'
            return
        }

        taskData.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'taskData.label', default: 'TaskData'), taskData.id])
                redirect taskData
            }
            '*' { respond taskData, [status: CREATED] }
        }
    }

    def edit(TaskData taskData) {
        respond taskData
    }

    @Transactional
    def update(TaskData taskData) {
        if (taskData == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (taskData.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond taskData.errors, view:'edit'
            return
        }

        taskData.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'taskData.label', default: 'TaskData'), taskData.id])
                redirect taskData
            }
            '*'{ respond taskData, [status: OK] }
        }
    }

    @Transactional
    def delete(TaskData taskData) {

        if (taskData == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        taskData.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'taskData.label', default: 'TaskData'), taskData.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'taskData.label', default: 'TaskData'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
