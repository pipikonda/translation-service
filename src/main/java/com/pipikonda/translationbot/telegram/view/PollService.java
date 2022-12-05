package com.pipikonda.translationbot.telegram.view;

import com.pipikonda.translationbot.telegram.dto.GetTranslationPollDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

@Service
@RequiredArgsConstructor
public class PollService {

    private static final String QUIZ_POLL_TYPE = "quiz";
    private final MessageSource messageSource;

    public SendPoll getTranslationPoll(GetTranslationPollDto dto) {
        return SendPoll.builder()
                .chatId(dto.getChatId())
                .type(QUIZ_POLL_TYPE)
                .question(messageSource.getMessage("telegram.poll.ask-translation", new String[]{dto.getAskedValue()}, dto.getUserLocale()))
                .correctOptionId(dto.getCorrectIndex())
                .options(dto.getOptions())
                .isAnonymous(false)
                .build();
    }
}
