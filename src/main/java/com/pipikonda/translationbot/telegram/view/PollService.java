package com.pipikonda.translationbot.telegram.view;

import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.telegram.dto.BotAnswerDto;
import com.pipikonda.translationbot.telegram.dto.GetTranslationPollDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PollService {

    private static final String QUIZ_POLL_TYPE = "quiz";
    private final MessageSource messageSource;

    public SendPoll getTranslationPoll(GetTranslationPollDto dto) {
        BotAnswerDto correctAnswer = dto.getOptions().stream()
                .filter(BotAnswerDto::isCorrect)
                .findFirst()
                .orElseThrow(() -> new BasicLogicException(ErrorCode.UNKNOWN_ERROR, "Not found correct answer in poll options"));
        List<String> values = dto.getOptions().stream()
                .map(BotAnswerDto::getValue)
                .toList();
        int correctIndex = IntStream.range(0, values.size())
                .filter(e -> values.get(0).equals(correctAnswer.getValue()))
                .findFirst()
                .orElseThrow(() -> new BasicLogicException(ErrorCode.UNKNOWN_ERROR, "Not found correct answer in poll options when expected"));
        return SendPoll.builder()
                .chatId(dto.getChatId())
                .type(QUIZ_POLL_TYPE)
                .question(messageSource.getMessage("telegram.poll.ask-translation", new String[]{dto.getAskedValue()}, dto.getUserLocale()))
                .correctOptionId(correctIndex)
                .options(values)
                .build();
    }
}
