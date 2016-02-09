package net.zomis.duga;

import net.zomis.duga.chat.BotConfiguration;
import net.zomis.duga.chat.StackExchangeChatBot;
import net.zomis.duga.chat.WebhookParameters;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;

public class DugaTest {

    public static void main(String[] args) {

        if (true) {
            System.out.println(new File("").getAbsolutePath());
            // return;
        }

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
        System.out.println("With password " + config.getBotPassword());

        config.setChatThrottle(10000);
        config.setChatMaxBurst(2);
        config.setChatMinimumDelay(500);
        Scanner scanner = new Scanner(System.in);
        StackExchangeChatBot bot = new StackExchangeChatBot(config);
        System.out.println("Press enter to start bot");
        // bot.start();
        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                break;
            }
            System.out.println("Input: " + input);
            bot.postMessages(WebhookParameters.toRoom("16134"),
                Collections.singletonList(input));
        }
        scanner.close();
    }

}
