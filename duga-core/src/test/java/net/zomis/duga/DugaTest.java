package net.zomis.duga;

import net.zomis.duga.chat.BotConfiguration;
import net.zomis.duga.chat.ChatBot;
import net.zomis.duga.chat.StackExchangeChatBot;
import net.zomis.duga.chat.WebhookParameters;
import net.zomis.duga.chat.events.DugaStartedEvent;
import net.zomis.duga.chat.events.DugaStopEvent;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class DugaTest {

    private static final WebhookParameters room = WebhookParameters.toRoom("16134");

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
        ChatBot bot = new StackExchangeChatBot(config);
        bot.registerListener(DugaStartedEvent.class, DugaTest::interactive);
        bot.registerListener(DugaStopEvent.class, DugaTest::shutdown);
        System.out.println("Starting bot...");
        bot.start();
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
    }

}
