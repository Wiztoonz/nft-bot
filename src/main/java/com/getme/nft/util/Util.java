package com.getme.nft.util;

import com.getme.nft.model.Nft;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.model.request.Keyboard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    public static Keyboard createKeyboard(String inputPlaceholder, String... rowButtonName) {
        String[][] rowButtons = Arrays.stream(rowButtonName)
                .map(List::of)
                .toList().stream()
                .map(buttonRows -> buttonRows.toArray(String[]::new))
                .toArray(String[][]::new);
        return new ReplyKeyboardMarkup(rowButtons, true, false, inputPlaceholder, true);
    }

    public static SendMessage sendMessage(Object chatId, String text, Keyboard keyboard) {
        return new SendMessage(chatId, text)
                .replyMarkup(keyboard)
                .parseMode(ParseMode.HTML);
    }

    public static List<SendPhoto> getNftPosts(Object chatId, List<Nft> nfts) {
        return nfts.stream().map(nft ->
                new SendPhoto(chatId, nft.imageLink())
                        .caption(
                                """
                                <strong>%s</strong>
                                
                                %s
                                
                                🔷<strong>PROJECT DETAILS</strong>🔷
                                
                                %s
                                
                                🔷<strong>PROJECT LINKS</strong>🔷
                                
                                %s
                                
                                %s
                                """.formatted(
                                        nft.name(),
                                        nft.description(),
                                        getDetails(nft.details()),
                                        getLinks(nft.links()),
                                        getTags(nft.tags()))
                        )).toList();
    }

    private static String getDetails(List<String> details) {
        return details.stream().map(detail -> "\t\t\t✅ " + "<em>"  + detail + "</em>").collect(Collectors.joining(System.lineSeparator()));
    }

    private static String getLinks(List<String> links) {
        return links.stream().map(link -> "\t\t\t✅ " + "<em>"  + link.replace(":<", "<") + "</em>").collect(Collectors.joining(System.lineSeparator()));
    }

    private static String getTags(List<String> tags) {
        return String.join(" ", tags);
    }

}
