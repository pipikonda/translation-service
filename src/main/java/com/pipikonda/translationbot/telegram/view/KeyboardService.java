package com.pipikonda.translationbot.telegram.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
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

    public InlineKeyboardMarkup getBackToMenuKeyboard(Locale userLocale) throws JsonProcessingException {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(getBackButton(userLocale)))
                .build();
    }

    private InlineKeyboardButton getBackButton(Locale userLocale) throws JsonProcessingException {
        return InlineKeyboardButton.builder()
                .text(messageSource.getMessage("telegram.button-name.main", null, userLocale))
                .callbackData(
                        objectMapper.writeValueAsString(CallbackDataDto.builder()
                                .command(CallbackDataCommand.BACK_TO_MENU)
                                .build())
                ).build();
    }

    public InlineKeyboardMarkup getMenuKeyboard(Locale userLocale) throws JsonProcessingException {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.translate", null, userLocale))
                                        .callbackData(
                                                objectMapper.writeValueAsString(CallbackDataDto.builder()
                                                        .command(CallbackDataCommand.TRANSLATE_WORD)
                                                        .build())
                                        ).build()
                        )
                ).keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.get-word", null, userLocale))
                                        .callbackData(
                                                objectMapper.writeValueAsString(CallbackDataDto.builder()
                                                        .command(CallbackDataCommand.GET_RANDOM_WORD)
                                                        .build())
                                        ).build()
                        )
                ).keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(messageSource.getMessage("telegram.button-name.bot-info", null, userLocale))
                                        .callbackData(
                                                objectMapper.writeValueAsString(CallbackDataDto.builder()
                                                        .command(CallbackDataCommand.GET_BOT_INFO)
                                                        .build())
                                        ).build()
                        )
                ).build();
    }

    public InlineKeyboardMarkup getPollKeyboard(List<String> options, Locale userLocale, Long repeatAttemptId) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("attempt", repeatAttemptId);
        List<List<InlineKeyboardButton>> pollButtons = options.stream()
                .map(e -> {
                    try {
                        return List.of(InlineKeyboardButton.builder()
                                .text(e)
                                .callbackData(objectMapper.writeValueAsString(CallbackDataDto.builder()
                                        .command(CallbackDataCommand.ANSWER)
                                        .params(objectNode.put("answer", e))
                                        .build()))
                                .build());
                    } catch (JsonProcessingException ex) {
                        log.error("Build poll options got exception", ex);
                        throw new RuntimeException(ex);
                    }
                }).toList();

        return InlineKeyboardMarkup.builder()
                .keyboard(pollButtons)
                .keyboardRow(List.of(getBackButton(userLocale)))
                .build();
    }
}
