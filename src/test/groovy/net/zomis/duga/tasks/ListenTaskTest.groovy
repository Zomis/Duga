package net.zomis.duga.tasks;

import net.zomis.duga.ChatCommands;
import net.zomis.duga.DugaBotService;
import net.zomis.duga.DugaChatListener;
import net.zomis.duga.chat.BotRoom;
import net.zomis.duga.chat.listen.ChatMessageIncoming;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

public class ListenTaskTest {

    @Test
    public void chatCommand() {

        ChatScrape.metaClass.static.fetch = {a -> return "Just a little something" }
        DugaChatListener bean = Mockito.mock(DugaChatListener.class);
        Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getProperty(Mockito.anyString(), Mockito.anyString())).thenReturn("@Duga do");
        Mockito.when(bean.getEnvironment()).thenReturn(env);
        DugaBotService bot = Mockito.mock(DugaBotService.class);
        Mockito.when(bot.room(Mockito.anyString())).thenReturn(new BotRoom(bot, "8595"));
        ListenTask task = new ListenTask(bot, "20298", Mockito.mock(ChatCommands.class),
            bean);
        ChatMessageIncoming message = command("programmers.classify true");
        task.botCommand(message);
    }

    private ChatMessageIncoming command(String text) {
        ChatMessageIncoming chatMessage = Mockito.mock(ChatMessageIncoming.class);
        Mockito.when(chatMessage.getContent()).thenReturn("@Duga do " + text);
        Mockito.when(chatMessage.cleanHTML()).thenReturn("@Duga do " + text);
        return chatMessage;
    }


}
