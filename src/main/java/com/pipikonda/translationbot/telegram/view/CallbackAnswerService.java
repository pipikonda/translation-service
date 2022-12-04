package com.pipikonda.translationbot.telegram.view;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

@Service
public class CallbackAnswerService {

    public AnswerCallbackQuery getCallbackAnswer(String callbackQueryId) {
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .build();
    }
}
