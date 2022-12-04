package com.pipikonda.translationbot.telegram.service.handlers.states;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pipikonda.translationbot.domain.BotUser;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface UserStateHandler {

    void handle(BotUser botUser, String word) throws TelegramApiException, JsonProcessingException;

    BotUser.UserState getUserState();
}
