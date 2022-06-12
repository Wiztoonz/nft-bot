package com.getme.nft;

import com.getme.nft.service.BotService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class App implements CommandLineRunner {

    private final TelegramBot bot;
    private final BotService botService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                botService.receiveUserMessage(update);
                if (update.callbackQuery() != null) {
                    botService.receiveUserCallback(update);
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

}
