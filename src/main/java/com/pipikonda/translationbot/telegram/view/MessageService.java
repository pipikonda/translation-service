package com.pipikonda.translationbot.telegram.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pipikonda.translationbot.telegram.dto.GetMessageBotRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final String HTML_PARSE_MODE = "HTML";
    private final MessageSource messageSource;
    private final KeyboardService keyboardService;

    public SendMessage getMenuMessage(Long chatId, Locale userLocale) throws JsonProcessingException {
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageSource.getMessage("telegram.message-text.menu", null, userLocale))
                .replyMarkup(keyboardService.getMenuKeyboard(userLocale))
                .build();
    }

    public SendMessage getMessageWithBackKeyboard(GetMessageBotRequestDto dto) throws JsonProcessingException {
        return SendMessage.builder()
                .chatId(dto.getChatId())
                .text(messageSource.getMessage(dto.getMessagePattern(), dto.getParams(), dto.getUserLocale()))
                .replyMarkup(keyboardService.getBackToMenuKeyboard(dto.getUserLocale()))
                .parseMode(HTML_PARSE_MODE)
                .build();
    }
}
