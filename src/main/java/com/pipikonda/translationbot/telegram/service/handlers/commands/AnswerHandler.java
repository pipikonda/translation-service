package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.service.RepeatAttemptService;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.service.BotUserService;
import com.pipikonda.translationbot.telegram.view.CallbackAnswerService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerHandler implements CommandHandler {

    private final BotUserService botUserService;
    private final RepeatAttemptService repeatAttemptService;
    private final CallbackAnswerService callbackAnswerService;
    private final MessageService messageService;
    private final TranslateBot translateBot;

    @Override
    public void handleCommand(Update update, BotUser botUser, CallbackDataDto data) throws TelegramApiException {
        botUserService.save(botUser.toBuilder()
                .userState(BotUser.UserState.ACTIVE)
                .lastStateChanged(Instant.now())
                .build());
        log.info("CallbackData is ===> {}", data);

        Long repeatAttemptId = data.getParams().get("attemptId").asLong();
        Long userAnswer = data.getParams().get("answerId").asLong();
        log.info("User answer for attempt {} is {}", repeatAttemptId, userAnswer);
        boolean answerCorrect = repeatAttemptService.saveAnswer(repeatAttemptId, userAnswer);

        String answerPattern = answerCorrect ? "telegram.poll.success-answer-result" : "telegram.poll.failed-answer-result";
        AnswerCallbackQuery translationPollAnswer =
                callbackAnswerService.getTranslationPollAnswer(update.getCallbackQuery().getId(), answerPattern, Locale.getDefault());
        translateBot.execute(translationPollAnswer);
        translateBot.execute(messageService.getDeleteMessage(botUser.getChatId(), update.getCallbackQuery().getMessage().getMessageId()));
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.ANSWER;
    }
}
