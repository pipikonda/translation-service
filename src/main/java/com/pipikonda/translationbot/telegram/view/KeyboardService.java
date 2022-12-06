package com.pipikonda.translationbot.telegram.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.dto.OptionDto;
import com.pipikonda.translationbot.telegram.service.handlers.CallbackDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeyboardService {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;
    private final CallbackDataMapper callbackDataMapper;

    public InlineKeyboardMarkup getBackToMenuKeyboard(Locale userLocale) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(getBackButton(userLocale)))
                .build();
    }

    private InlineKeyboardButton getBackButton(Locale userLocale) {
        return InlineKeyboardButton.builder()
                .text(messageSource.getMessage("telegram.button-name.main", null, userLocale))
                .callbackData(
                        callbackDataMapper.callbackDataToString(CallbackDataDto.builder()
                                .command(CallbackDataCommand.BACK_TO_MENU)
                                .build())
                ).build();
    }

    public InlineKeyboardMarkup getMenuKeyboard(Locale userLocale) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.translate", null, userLocale))
                                        .callbackData(
                                                callbackDataMapper.callbackDataToString(CallbackDataDto.builder()
                                                        .command(CallbackDataCommand.TRANSLATE_WORD)
                                                        .build())
                                        ).build()
                        )
                ).keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.get-word", null, userLocale))
                                        .callbackData(
                                                callbackDataMapper.callbackDataToString(CallbackDataDto.builder()
                                                        .command(CallbackDataCommand.GET_RANDOM_WORD)
                                                        .build())
                                        ).build()
                        )
                ).keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.bot-info", null, userLocale))
                                        .callbackData(
                                                callbackDataMapper.callbackDataToString(CallbackDataDto.builder()
                                                        .command(CallbackDataCommand.GET_BOT_INFO)
                                                        .build())
                                        ).build()
                        )
                ).build();
    }

    public InlineKeyboardMarkup getPollKeyboard(List<OptionDto> options, Locale userLocale, Long repeatAttemptId) {
        List<List<InlineKeyboardButton>> pollButtons = options.stream()
                .map(e -> List.of(InlineKeyboardButton.builder()
                        .text(e.getValue())
                        .callbackData(callbackDataMapper.callbackDataToString(CallbackDataDto.builder()
                                .command(CallbackDataCommand.ANSWER)
                                .params(objectMapper.createObjectNode()
                                        .put("attemptId", repeatAttemptId)
                                        .put("answerId", e.getAnswerId()))
                                .build()))
                        .build())
                ).toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(pollButtons)
                .keyboardRow(List.of(getBackButton(userLocale)))
                .build();
    }
}
