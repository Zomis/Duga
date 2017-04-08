import grails.util.BuildSettings
import grails.util.Environment

def logPath = '.'
def env = System.getenv()

if (env['TOMCAT_LOGS']) {
    logPath = env['TOMCAT_LOGS']
}

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

logger('org.springframework.boot.autoconfigure.security', INFO)
root(INFO, ['STDOUT'])

appender("dugaTasks", FileAppender) {

    file = "$logPath/dugaTasks.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}
appender("dugaChat", FileAppender) {

    file = "$logPath/dugaChat.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

logger("net.zomis.duga.chat.listen", DEBUG, ['dugaTasks', 'STDOUT'], false )
logger("net.zomis.duga.tasks", DEBUG, ['dugaTasks', 'STDOUT'], false )

logger("net.zomis.duga.chat", DEBUG, ['dugaChat', 'STDOUT'], false )

if(Environment.current == Environment.PRODUCTION) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "$logPath/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false )
}
