package com.pipikonda.translationbot.telegram.view;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CallbackAnswerService {

    private final MessageSource messageSource;

    public AnswerCallbackQuery getCallbackAnswer(String callbackQueryId) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .build();
    }

    public AnswerCallbackQuery getTranslationPollAnswer(String callbackQueryId, String pattern, Locale userLocale) {
        return AnswerCallbackQuery.builder()
                .text(messageSource.getMessage(pattern, null, userLocale))
                .callbackQueryId(callbackQueryId)
                .build();
    }
}
