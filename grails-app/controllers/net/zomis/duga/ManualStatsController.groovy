package net.zomis.duga

import org.grails.web.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class ManualStatsController {

    private static final Logger logger = LoggerFactory.getLogger(ManualStatsController.class)

    static allowedMethods = [stats:'POST']

    @Autowired
    DynamicStats dynamicStats

    def stats() {
        JSONObject json = request.JSON

        def result = dynamicStats.save(json)
        render('Current: ' + result)
    }

}
