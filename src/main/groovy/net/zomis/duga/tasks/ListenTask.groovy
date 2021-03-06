package net.zomis.duga.tasks

import com.gistlabs.mechanize.impl.MechanizeAgent
import groovy.transform.CompileStatic
import groovy.transform.TimedInterrupt
import net.zomis.duga.ChatCommands
import net.zomis.duga.DugaBotService
import net.zomis.duga.DugaChatListener
import net.zomis.duga.chat.ChatMessage
import net.zomis.duga.chat.ChatMessageHelper
import net.zomis.duga.chat.listen.ChatMessageIncoming
import net.zomis.duga.chat.BotRoom
import net.zomis.duga.chat.listen.StackExchangeFetch
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ScheduledFuture

import static org.codehaus.groovy.syntax.Types.*

class ListenTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ListenTask.class);

    public static final String CONFIG_COMMAND_PREFIX_KEY = 'commandPrefix'
    public static final String CONFIG_COMMAND_PREFIX_DEFAULT = '@Duga '

    private final String commandPrefix
    private final DugaBotService bot
    private final String room
    private final BotRoom params
    private final DugaChatListener bean
    @Deprecated
    private final ChatCommands handler
    private final GroovyShell groovyShell
    private MechanizeAgent agent
    private final net.zomis.duga.chat.listen.ListenTask domainTask;
    ScheduledFuture<?> future

    public ListenTask(DugaBotService bot, String room, ChatCommands commandHandler, DugaChatListener bean) {
        this.bean = bean
        this.bot = bot
        this.room = room
        this.params = bot.room(room)
        this.handler = commandHandler
        this.agent = new MechanizeAgent()
        this.commandPrefix = bean.environment.getProperty(CONFIG_COMMAND_PREFIX_KEY, CONFIG_COMMAND_PREFIX_DEFAULT)
        this.domainTask = new net.zomis.duga.chat.listen.ListenTask(bot, new StackExchangeFetch({bot.fkey()}), room,
            {this.handle(it)})

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
                    Boolean.TYPE,
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
        scz.addExpressionCheckers(new SecureASTCustomizer.ExpressionChecker() {
            @Override
            boolean isAuthorized(Expression expression) {
                if (expression instanceof VariableExpression) {
                    VariableExpression expr = (VariableExpression) expression
                    return expr.name != 'bean' && expr.name != 'message'
                }
                return true
            }
        })

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

    private void handle(ChatMessageIncoming message) {
        def content = message.content
        if (!content.startsWith(commandPrefix)) {
            return
        }
        if (!authorizedCommander(message)) {
            return
        }
        message.cleanHTML()
        logger.info("possible command: $message.content")
        def commandResult = botCommand(message)
        if (commandResult != null) {
            String text = String.valueOf(commandResult);
            List<ChatMessage> response = ChatMessageHelper.split(message.createReply('Result: ' + text));
            response.forEach({it.post()})
        }
    }

    synchronized void latestMessages() {
        domainTask.run()
    }

    def botCommand(ChatMessageIncoming chatMessageIncoming) {
        try {
            String command = chatMessageIncoming.cleanHTML().substring(commandPrefix.length())
            ChatCommandDelegate script = (ChatCommandDelegate) groovyShell.parse(command)
            script.init(chatMessageIncoming, bean)
            def result = script.run()
            logger.info('Script ' + chatMessageIncoming + ' returned ' + result)
            if (result instanceof Map) {
                Map res = (Map) result
                Object obj = res.get('default')
                if (obj != null) {
                    logger.info('default key found in map, returned ' + obj)
                    result = obj
                }
            }
            if (result instanceof Closure) {
                result = result.call()
                logger.info('Closure ' + chatMessageIncoming + ' returned ' + result)
            }
            return result
        } catch (Exception ex) {
            logger.error('Script ' + chatMessageIncoming + ' caused exception', ex)
            String mess = ex.toString()
            chatMessageIncoming.reply(mess.substring(0, Math.min(420, mess.length())))
//            bot.postDebug(chatMessageIncoming.toString() + ' caused exception: ' + ex)
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
