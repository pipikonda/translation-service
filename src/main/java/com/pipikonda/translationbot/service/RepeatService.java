package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.controller.dto.CreateRepeatDto;
import com.pipikonda.translationbot.controller.dto.RepeatAttemptDto;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.BotUserRepository;
import com.pipikonda.translationbot.repository.RepeatRepository;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.GetTranslationPollDto;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepeatService {

    private final RepeatRepository repeatRepository;
    private final RepeatAttemptService repeatAttemptService;
    private final TranslateBot translateBot;
    private final BotUserRepository botUserRepository;
    private final MessageService messageService;

    public Repeat createNewRepeat(CreateRepeatDto dto) {
        Instant nextRepeat =
                dto.getImmediatelyRepeat() ?
                        Instant.now() :
                        Instant.now().plus(RepeatAttemptService.baseRepeatInterval, RepeatAttemptService.repeatIntervalUnit);
        //todo if immediatelyRepeat -> call poll async

        return repeatRepository.save(Repeat.builder()
                .userId(dto.getUserId())
                .nextRepeat(nextRepeat)
                .wordTranslationId(dto.getWordTranslationId())
                .build());
    }

    public boolean checkRepeatPresent(String userId, Long wordTranslationId) {
        return repeatRepository.findByUserIdAndWordTranslationId(userId, wordTranslationId)
                .isPresent();
    }

    @Scheduled(fixedDelay = 60000L)
    public void createRepeatAttempts() {
        log.info("Start createRepeatAttempts scheduler");
        Long repeatId;
        while ((repeatId = repeatRepository.getNextRepeat(Instant.now())) != null) {
            try {
                String userId = repeatRepository.findById(repeatId)
                        .map(Repeat::getUserId)
                        .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found repeat when expected"));
                BotUser botUser = botUserRepository.findById(Long.valueOf(userId))
                        .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found user by id " + userId + " when expected"));
                RepeatAttemptDto repeatAttempt = repeatAttemptService.createRepeatAttempt(repeatId);
                SendMessage translationPollMessage = messageService.getTranslatePollKeyboard(GetTranslationPollDto.builder()
                        .options(repeatAttempt.getValues())
                        .askedValue(repeatAttempt.getAskedValue())
                        .chatId(botUser.getChatId())
                        .userLocale(Locale.getDefault())
                        .repeatAttempt(repeatAttempt.getAttemptId())
                        .build());
                log.info("Poll is {}", translationPollMessage);
                translateBot.execute(translationPollMessage);
            } catch (Exception ex) {
                log.warn("Scheduled method got exception ", ex);
            }
        }
    }
}
