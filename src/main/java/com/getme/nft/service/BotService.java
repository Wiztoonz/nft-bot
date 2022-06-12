package com.getme.nft.service;

import com.pengrad.telegrambot.model.Update;

public interface BotService {

    void receiveUserMessage(Update update);

    void receiveUserCallback(Update update);

}
