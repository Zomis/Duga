import grails.util.BuildSettings
import grails.util.Environment

def logPath = '.'
def env = System.getenv()
println("LOGBACK TEST: Environment variables are $env")
println("LOGBACK TEST: Current path is " + (new File(".")).getAbsolutePath())

if (env['TOMCAT_LOGS']) {
    logPath = env['TOMCAT_LOGS']
}
logPath = "/var/lib/tomcat8/logs"

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] %level %logger - %msg%n"
    }
}

logger('org.springframework.boot.autoconfigure.security', INFO)

appender("duga", FileAppender) {

    file = "$logPath/duga.log"
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] %level %logger - %msg%n"
    }
}

logger("net.zomis.duga.chat.listen", DEBUG, ['duga'], false )
logger("net.zomis.duga.tasks", DEBUG, ['duga'], false )
logger("net.zomis.duga.chat", DEBUG, ['duga'], false )
root(INFO, ['duga'])

if(Environment.current == Environment.PRODUCTION) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "$logPath/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] %level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false )
}
