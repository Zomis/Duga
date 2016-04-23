package net.zomis.duga.tasks;

import net.zomis.duga.ChatCommands;
import net.zomis.duga.DugaBotService;
import net.zomis.duga.DugaChatListener
import net.zomis.duga.DugaMachineLearning;
import net.zomis.duga.chat.BotRoom;
import net.zomis.duga.chat.listen.ChatMessageIncoming
import net.zomis.machlearn.text.TextClassification
import net.zomis.machlearn.text.TextFeatureMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment

import java.util.function.UnaryOperator;

public class ListenTaskTest {

    @Test
    public void chatCommand() {
        DugaChatListener bean = Mockito.mock(DugaChatListener.class);
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getProperty(Mockito.anyString(), Mockito.anyString())).thenReturn("@Duga do ");
        Mockito.when(bean.getEnvironment()).thenReturn(env);

        DugaMachineLearning learning = new DugaMachineLearning()
        learning.programmers = new TextClassification({s -> s} as UnaryOperator<String>,
                new TextFeatureMapper("comment", "programmers", "Stack Overflow", "404"),
        [0.2, 0.1, -0.15, 0.4] as double[], 0.3);
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
        task.botCommand(message);
    }

    private ChatMessageIncoming command(String text) {
        ChatMessageIncoming chatMessage = Mockito.mock(ChatMessageIncoming.class);
        Mockito.when(chatMessage.getContent()).thenReturn("@Duga do " + text);
        Mockito.when(chatMessage.cleanHTML()).thenReturn("@Duga do " + text);
        Mockito.when(chatMessage.getParentId()).thenReturn(42L);
        return chatMessage;
    }


}
