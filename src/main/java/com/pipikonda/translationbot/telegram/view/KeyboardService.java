package com.pipikonda.translationbot.telegram.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class KeyboardService {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    public InlineKeyboardMarkup getBackToMenuKeyboard(Locale userLocale) throws JsonProcessingException {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder()
                                .text(messageSource.getMessage("telegram.button-name.main", null, userLocale))
                                .callbackData(
                                        objectMapper.writeValueAsString(CallbackDataDto.builder()
                                                .value(CallbackDataCommand.BACK_TO_MENU)
                                                .build())
                                ).build()
                ))
                .build();
    }

    public InlineKeyboardMarkup getMenuKeyboard(Locale userLocale) throws JsonProcessingException {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.translate", null, userLocale))
                                        .callbackData(
                                                objectMapper.writeValueAsString(CallbackDataDto.builder()
                                                        .value(CallbackDataCommand.TRANSLATE_WORD)
                                                        .build())
                                        ).build()
                        )
                ).keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.get-word", null, userLocale))
                                        .callbackData(
                                                objectMapper.writeValueAsString(CallbackDataDto.builder()
                                                        .value(CallbackDataCommand.GET_RANDOM_WORD)
                                                        .build())
                                        ).build()
                        )
                ).keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.bot-info", null, userLocale))
                                        .callbackData(
                                                objectMapper.writeValueAsString(CallbackDataDto.builder()
                                                        .value(CallbackDataCommand.GET_BOT_INFO)
                                                        .build())
                                        ).build()
                        )
                ).build();
    }
}