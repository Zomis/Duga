package net.zomis.duga.tasks;

import net.zomis.duga.ChatCommands;
import net.zomis.duga.DugaBotService;
import net.zomis.duga.DugaChatListener
import net.zomis.duga.DugaGit
import net.zomis.duga.DugaMachineLearning;
import net.zomis.duga.chat.BotRoom;
import net.zomis.duga.chat.listen.ChatMessageIncoming
import net.zomis.machlearn.text.TextClassification
import net.zomis.machlearn.text.TextFeatureMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment
import org.springframework.mock.env.MockEnvironment

import java.util.function.UnaryOperator;

public class ListenTaskTest {

    @Test
    public void chatCommand() {
        DugaChatListener bean = Mockito.mock(DugaChatListener.class);
        Environment env = new MockEnvironment();
        env.setProperty(ListenTask.CONFIG_COMMAND_PREFIX_KEY, "@Duga do ");
        Mockito.when(bean.getEnvironment()).thenReturn(env);

        DugaGit dugaGit = new DugaGit()
        dugaGit.environment = bean.getEnvironment()
        dugaGit.afterPropertiesSet()
        Mockito.when(bean.getDugaGit()).thenReturn(dugaGit);

        DugaMachineLearning learning = new DugaMachineLearning()
        learning.programmers = new TextClassification({s -> s} as UnaryOperator<String>,
                new TextFeatureMapper("comment", "programmers", "Stack Overflow", "404"),
        [0.05, 0.2, 0.1, -0.15, 0.4] as double[], 0.3);
        Mockito.when(bean.getLearning()).thenReturn(learning);

        ChatScrape scrape = Mockito.mock(ChatScrape.class);
        Mockito.when(scrape.fetch(42)).thenReturn("This is a comment mentioning programmers," +
                " which does not belong on Stack Overflow");
        Mockito.when(bean.getChatScrape()).thenReturn(scrape);
        DugaBotService bot = Mockito.mock(DugaBotService.class);
        Mockito.when(bot.room(Mockito.anyString())).thenReturn(new BotRoom(bot, "8595"));
        ListenTask task = new ListenTask(bot, "20298", Mockito.mock(ChatCommands.class),
            bean);
        ChatMessageIncoming message = command("programmers.features");
        def result = task.botCommand(message);
        assert result.toString().contains("comment:0.2")

        message = command("programmers.classify true");
        result = task.botCommand(message);
        println result
    }

    private static ChatMessageIncoming command(String text) {
        ChatMessageIncoming chatMessage = Mockito.mock(ChatMessageIncoming.class);
        String message = "@Duga do " + text;
        Mockito.when(chatMessage.getContent()).thenReturn(message);
        Mockito.when(chatMessage.cleanHTML()).thenReturn(message);
        Mockito.when(chatMessage.getParentId()).thenReturn(42L);
        Mockito.when(chatMessage.getUserId()).thenReturn(42L);
        Mockito.when(chatMessage.getUserName()).thenReturn("Gradle Build");
        Mockito.when(chatMessage.getMessageId()).thenReturn(2147483647L);
        Mockito.when(chatMessage.toString()).thenReturn("Mocked: [" + message + "]");
        return chatMessage;
    }


}
