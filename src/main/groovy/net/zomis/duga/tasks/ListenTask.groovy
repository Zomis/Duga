package net.zomis.duga.tasks

import com.gistlabs.mechanize.Resource
import com.gistlabs.mechanize.document.json.JsonDocument
import com.gistlabs.mechanize.document.json.node.JsonNode
import com.gistlabs.mechanize.impl.MechanizeAgent
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.TimedInterrupt
import net.zomis.duga.ChatCommands
import net.zomis.duga.DugaBot
import net.zomis.duga.DugaChatListener
import net.zomis.duga.chat.WebhookParameters
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer

import static org.codehaus.groovy.syntax.Types.*

class ListenTask implements Runnable {

    private static final int NUM_MESSAGES = 10

    private final String commandPrefix
    private final DugaBot bot
    private final String room
    private final WebhookParameters params
    private final DugaChatListener bean
    private final ChatCommands handler
    private final GroovyShell groovyShell
    private long lastHandledId
    private long lastMessageTime
    private MechanizeAgent agent

    public ListenTask(DugaBot bot, String room, ChatCommands commandHandler, DugaChatListener bean) {
        this.bean = bean
        this.bot = bot
        this.room = room
        this.params = WebhookParameters.toRoom(room)
        this.handler = commandHandler
        this.agent = new MechanizeAgent()
        this.commandPrefix = bean.environment.getProperty('commandPrefix', '@Duga ')

        Binding binding = new Binding()
        CompilerConfiguration cc = new CompilerConfiguration()

        def scz = new SecureASTCustomizer()
        scz.with {
            closuresAllowed = false // user will not be able to write closures
            methodDefinitionAllowed = false // user will not be able to define methods
            importsWhitelist = [ 'org.springframework.beans.factory.annotation.Autowired' ] // empty whitelist means imports are disallowed
            staticImportsWhitelist = [] // same for static imports
            staticStarImportsWhitelist = ['java.lang.Math'] // only java.lang.Math is allowed
            // the list of tokens the user can find
            // constants are defined in org.codehaus.groovy.syntax.Types
            tokensWhitelist = [
                    // ASSIGN,// Assignments is a security risk, as it allows `def abc = System; abc.exit(1);`
                    PLUS, MINUS, MULTIPLY, DIVIDE, MOD,
                    POWER, PLUS_PLUS, MINUS_MINUS, COMPARE_EQUAL,
                    COMPARE_NOT_EQUAL, COMPARE_LESS_THAN, COMPARE_LESS_THAN_EQUAL,
                    COMPARE_GREATER_THAN, COMPARE_GREATER_THAN_EQUAL,
            ].asImmutable()
            // limit the types of constants that a user can define to number types only
            constantTypesClassesWhiteList = [
                    String,
                    Object, // simply typing `ping` to invoke ChatCommandDelegate.ping requires this
                    Integer,
                    Float,
                    Long,
                    Double,
                    BigDecimal,
                    Integer.TYPE,
                    Long.TYPE,
                    Float.TYPE,
                    Double.TYPE
            ].asImmutable()
            // method calls are only allowed if the receiver is of one of those types
            // be careful, it's not a runtime type!
            receiversClassesWhiteList = [
                    Object, // compiler believes that any method call is a call on Object, it is not aware of the delegate class
                    Math,
                    Integer,
                    Float,
                    Double,
                    Long,
                    BigDecimal
            ].asImmutable()
        }
        cc.addCompilationCustomizers(scz)
        def compileStaticCustomizer = new ASTTransformationCustomizer(Collections.singletonMap('extensions',
            Collections.singletonList('typecheck-extension.groovy')), CompileStatic.class)
        cc.addCompilationCustomizers(compileStaticCustomizer)
        Map timeoutOptions = new HashMap()
        timeoutOptions.put('value', 5)
        def timeoutCustomizer = new ASTTransformationCustomizer(timeoutOptions, TimedInterrupt.class)
        cc.addCompilationCustomizers(timeoutCustomizer)
        cc.setScriptBaseClass(ChatCommandDelegate.class.getName())
        this.groovyShell = new GroovyShell(getClass().getClassLoader(), binding, cc)
    }

    synchronized void latestMessages() {
        Map<String, String> parameters = new HashMap<>();
        String fkey = bot.fkey()
        if (fkey == null) {
            return
        }
        parameters.put("fkey", fkey);
        parameters.put("mode", "messages");
        parameters.put("msgCount", String.valueOf(NUM_MESSAGES));
        Resource response = agent.post("http://chat.stackexchange.com/chats/" + room + "/events", parameters)

        if (!(response instanceof JsonDocument)) {
            println 'Unexpected response: ' + response
            return
        }

        println 'Checking for events in room ' + room
        def jsonDocument = response as JsonDocument
        JsonNode node = jsonDocument.root
        def json = new JsonSlurper().parseText(node.toString())
        def events = json.events
        long previousId = lastHandledId
        for (def event in events) {
            ChatMessageIncoming message = new ChatMessageIncoming(event as Map)
            message.bot = bot
            message.params = params
            if (message.message_id <= lastHandledId) {
                continue
            }
            lastHandledId = Math.max(lastHandledId, message.message_id)
            lastMessageTime = Math.max(lastMessageTime, message.time_stamp)
            if (previousId <= 0) {
                println 'Previous id 0, skipping ' + message.content
                continue
            }
            def content = message.content
            if (!content.startsWith(commandPrefix)) {
                continue
            }
            if (!authorizedCommander(message)) {
                continue
            }
            message.cleanHTML()
            println "possible command: $message.content"
            def commandResult = botCommand(message)
            if (commandResult != null) {
                message.reply('Result: ' + String.valueOf(commandResult))
            }
//            handler.botCommand(message)
        }
/*            Root node: {"ms":4,"time":41194973,"sync":1433551091,"events":
                [{"room_id":16134,"event_type":1,"time_stamp":1433547911,"user_id":125580,"user_name":"Duga","message_id":22039309,"content":"Loki Astari vs. Simon Andr&#233; Forsberg: 4383 diff. Year: -1368. Quarter: -69. Month: -5. Week: +60. Day: -25."}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433548817,"user_id":125580,"user_name":"Duga","message_id":22039366,"content":"<b><i>RELOAD!<\/i><\/b>"}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433548849,"user_id":125580,"user_name":"Duga","message_id":22039371,"content":"&#91;<a href=\"https://github.com/retailcoder/Rubberduck\" rel=\"nofollow\"><b>retailcoder/Rubberduck<\/b><\/a>&#93; 12 commits. 2 closed issues. 5 issue comments."}
                 ,{"room_id":16134,"event_type":1,"time_stamp":1433551821,"user_id":98071,"user_name":"Simon André Forsberg","parent_id":22039371,"show_parent":true,"message_id":22039768,"content":"@Duga No open issues today?"}]}

        Success: {"id":22039802,"time":1433552063}
*/
    }

    def botCommand(ChatMessageIncoming chatMessageIncoming) {
        try {
            ChatCommandDelegate script = (ChatCommandDelegate) groovyShell.parse(chatMessageIncoming.content.substring(commandPrefix.length()))
            script.init(chatMessageIncoming, bean)
            def result = script.run()
            println 'Script ' + chatMessageIncoming + ' returned ' + result
            if (result instanceof Map) {
                Map res = (Map) result
                Object obj = res.get('default')
                if (obj != null) {
                    println 'default key found in map, returned ' + obj
                    result = obj
                }
            }
            if (result instanceof Closure) {
                result = result.call()
                println 'Closure ' + chatMessageIncoming + ' returned ' + result
            }
            return result
        } catch (Exception ex) {
            println 'Script ' + chatMessageIncoming + ' caused exception: ' + ex
            String mess = ex.toString()
            chatMessageIncoming.reply(mess.substring(0, Math.min(420, mess.length())))
//            bot.postDebug(chatMessageIncoming.toString() + ' caused exception: ' + ex)
            ex.printStackTrace(System.out)
            return null
        }
    }

    boolean authorizedCommander(ChatMessageIncoming message) {
        true
    }

    @Override
    void run() {
        latestMessages()
    }
}
