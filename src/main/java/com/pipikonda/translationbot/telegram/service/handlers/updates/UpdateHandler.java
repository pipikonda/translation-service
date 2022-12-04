package com.pipikonda.translationbot.telegram.service.handlers.updates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pipikonda.translationbot.telegram.dto.UpdateType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface UpdateHandler {

    void handleUpdate(Update update) throws TelegramApiException, JsonProcessingException;

    Long getChatId(Update update);

    UpdateType getUpdateType();
}
