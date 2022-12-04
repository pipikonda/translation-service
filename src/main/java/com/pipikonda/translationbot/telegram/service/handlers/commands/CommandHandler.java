package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CommandHandler {

    void handleCommand(String queryId, BotUser botUser) throws TelegramApiException, JsonProcessingException;

    CallbackDataCommand getCommand();
}
