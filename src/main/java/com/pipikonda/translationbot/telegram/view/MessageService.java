package com.pipikonda.translationbot.telegram.view;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.telegram.dto.GetMessageBotRequestDto;
import com.pipikonda.translationbot.telegram.dto.GetSettingsInfoDto;
import com.pipikonda.translationbot.telegram.dto.GetTranslationPollDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final String HTML_PARSE_MODE = "HTML";
    private final MessageSource messageSource;
    private final KeyboardService keyboardService;

    public SendMessage getMenuMessage(Long chatId, Locale userLocale) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageSource.getMessage("telegram.message-text.menu", null, userLocale))
                .replyMarkup(keyboardService.getMenuKeyboard(userLocale))
                .build();
    }

    public SendMessage getSettingsMessage(GetSettingsInfoDto dto) {
        return SendMessage.builder()
                .chatId(dto.getChatId())
                .text(messageSource.getMessage("telegram.message-text.settings", dto.getParams(), dto.getUserLocale()))
                .replyMarkup(keyboardService.getSettingsKeyboard(dto.getUserLocale()))
                .build();
    }

    public SendMessage setSourceLangMessage(Long chatId, Locale userLocale) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageSource.getMessage("telegram.message-text.set-source-lang", null, userLocale))
                .replyMarkup(keyboardService.getSourceLangKeyboard(userLocale))
                .build();
    }

    public SendMessage setTargetLangMessage(Long chatId, Locale userLocale, Lang sourceLang) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageSource.getMessage("telegram.message-text.set-target-lang", null, userLocale))
                .replyMarkup(keyboardService.getTargetLangKeyboard(userLocale, sourceLang))
                .build();
    }

    public SendMessage getMessageWithBackKeyboard(GetMessageBotRequestDto dto) {
        return SendMessage.builder()
                .chatId(dto.getChatId())
                .text(messageSource.getMessage(dto.getMessagePattern(), dto.getParams(), dto.getUserLocale()))
                .replyMarkup(keyboardService.getBackToMenuKeyboard(dto.getUserLocale()))
                .parseMode(HTML_PARSE_MODE)
                .build();
    }

    public SendMessage getTranslatePollKeyboard(GetTranslationPollDto dto) {
        return SendMessage.builder()
                .chatId(dto.getChatId())
                .text(messageSource.getMessage("telegram.poll.ask-translation", new String[]{dto.getAskedValue()}, dto.getUserLocale()))
                .replyMarkup(keyboardService.getPollKeyboard(dto.getOptions(), dto.getUserLocale(), dto.getRepeatAttempt()))
                .parseMode(HTML_PARSE_MODE)
                .build();
    }

    public DeleteMessage getDeleteMessage(Long chatId, Integer messageId) {
        return DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }
}
