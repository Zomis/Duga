package net.zomis.duga;

import net.zomis.duga.chat.BotConfiguration;
import net.zomis.duga.chat.BotRoom;
import net.zomis.duga.chat.ChatBot;
import net.zomis.duga.chat.StackExchangeChatBot;
import net.zomis.duga.chat.events.DugaStartedEvent;
import net.zomis.duga.chat.events.DugaStopEvent;
import net.zomis.duga.chat.listen.ChatMessageIncoming;
import net.zomis.duga.chat.listen.ListenTask;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DugaTest {

    private static final BotRoom room = BotRoom.toRoom("20298");
    static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {

        BotConfiguration config = new BotConfiguration();

        config.setRootUrl("http://stackexchange.com");
        config.setChatUrl("http://chat.stackexchange.com");

        try {
            Properties properties = new Properties();
            properties.load(new FileReader("duga.conf"));
            config.setBotEmail(properties.getProperty("email").trim());
            config.setBotPassword(properties.getProperty("password").trim());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read 'duga.conf'", e);
        }

        System.out.println("Using email " + config.getBotEmail());

        config.setChatThrottle(10000);
        config.setChatMaxBurst(2);
        config.setChatMinimumDelay(500);
        StackExchangeChatBot bot = new StackExchangeChatBot(config);
        bot.registerListener(DugaStartedEvent.class,
            e -> new Thread(() -> interactive(e)).start());
        bot.registerListener(DugaStopEvent.class, DugaTest::shutdown);
        System.out.println("Starting bot...");
        bot.start();
        System.out.println("Bot started.");
        scheduler.scheduleAtFixedRate(new ListenTask(bot, bot.listener(), "20298", DugaTest::handle), 0, 3000, TimeUnit.MILLISECONDS);
    }

    private static void handle(ChatMessageIncoming chatMessageIncoming) {
        System.out.println(chatMessageIncoming);
        if (chatMessageIncoming.getContent().equals("Monking!")) {
            chatMessageIncoming.reply("Monking!");
        }
    }

    private static void shutdown(DugaStopEvent event) {
        event.getBot().postNow(room.message("Shutting down!"));
    }

    private static void interactive(DugaStartedEvent event) {
        System.out.println("Bot started and ready.");
        Scanner scanner = new Scanner(System.in);
        ChatBot bot = event.getBot();
        bot.postNow(room.message("Hello World!"));
        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                break;
            }
            System.out.println("Input: " + input);
            bot.postNow(room.message(input));
        }
        bot.stop();
        scanner.close();
        scheduler.shutdownNow();
    }

}
