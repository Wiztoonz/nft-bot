package com.getme.nft.service.impl;

import com.getme.nft.model.ChannelAdmin;
import com.getme.nft.service.BotService;
import com.getme.nft.service.NftService;
import com.getme.nft.util.Util;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    private final TelegramBot bot;
    private final NftService nftService;
    @Value("${ADMIN_ID}")
    private String adminId;
    @Value("${CHANNEL_ID}")
    private String channelId;

    @Override
    public void receiveUserMessage(Update update) {
        Message message = update.message();
        if (message != null) {
            Chat chat = message.chat();
            Long chatId = chat.id();
            String text = message.text();
            if (text != null && !text.isEmpty()) {
                switch (text.toLowerCase()) {
                    case "/start" -> {
                        String btnName = "NFT DROPS";
                        SendMessage start = Util.sendMessage(chatId,
                                """
                                        Hello \uD83D\uDC4B, click the \uD83D\uDC49<code>%s</code> \uD83D\uDC49button
                                        to get <strong>today's NFT</strong> information. \uD83D\uDE0E
                                        """.formatted(btnName),
                                Util.createKeyboard("NFT DROPS", btnName));
                        bot.execute(start, new Callback() {
                            @Override
                            public void onResponse(BaseRequest request, BaseResponse response) { }
                            @Override
                            public void onFailure(BaseRequest request, IOException e) { }
                        });
                    }
                    case "nft drops" -> {
                        List<SendPhoto> nftPosts = Util.getNftPosts(chatId, nftService.getNfts());
                        ChannelAdmin channelAdmin = new ChannelAdmin(Long.valueOf(adminId), channelId);
                        User user = message.from();
                        addAdminControl(channelAdmin, user, nftPosts).forEach(post -> {
                            bot.execute(post, new Callback() {
                                @Override
                                public void onResponse(BaseRequest request, BaseResponse response) { }
                                @Override
                                public void onFailure(BaseRequest request, IOException e) { }
                            });
                        });
                    }
                    default -> {
                        DeleteMessage deleteMessage = new DeleteMessage(chatId, message.messageId());
                        bot.execute(deleteMessage);
                    }
                }
            }
        }
    }

    @Override
    public void receiveUserCallback(Update update) {
        CallbackQuery callback = update.callbackQuery();
        if (callback != null) {
            String data = callback.data();
            if (data != null) {
                switch (data.toLowerCase()) {
                    case "/send-post" -> {
                        Message message = callback.message();
                        PhotoSize[] photoSizes = callback.message().photo();
                        List<SendPhoto> posts = Arrays.stream(photoSizes)
                                .findFirst()
                                .map(photo -> new SendPhoto(channelId, photo.fileId()).caption(message.caption()).parseMode(ParseMode.HTML)).stream()
                                .toList();
                        posts.forEach(post -> {
                            bot.execute(post, new Callback() {
                                @Override
                                public void onResponse(BaseRequest request, BaseResponse response) { }
                                @Override
                                public void onFailure(BaseRequest request, IOException e) { }
                            });
                        });

                    }
                }
            }
        }
    }

    private List<SendPhoto> addAdminControl(ChannelAdmin channelAdmin, User user, List<SendPhoto> posts) {
        if (channelAdmin != null && channelAdmin.adminId().equals(user.id())) {
            return posts.stream()
                    .map(post ->  post.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("ОТПРАВИТЬ").callbackData("/send-post"))))
                    .toList();
        }
        return posts;
    }

}

