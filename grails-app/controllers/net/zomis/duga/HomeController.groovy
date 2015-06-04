package net.zomis.duga

import org.springframework.beans.factory.annotation.Autowired

class HomeController {

    @Autowired
    DugaData data

    def index() {
    	render "You are in the home controller: " + data.data.get()
    }
}
