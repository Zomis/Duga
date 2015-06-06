package net.zomis.duga

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

import javax.annotation.PostConstruct

@Configuration
class DugaInit {

    @Autowired
    private TaskScheduler scheduler;

    @Autowired
    private DugaData duga;

    def runnable = {DugaData duga ->
        return {
            println 'Test ' + duga.data.addAndGet(1)
        }
    }

    @PostConstruct
    public void startup() {
        scheduler.scheduleAtFixedRate(runnable(duga), 10000)
    }

}
