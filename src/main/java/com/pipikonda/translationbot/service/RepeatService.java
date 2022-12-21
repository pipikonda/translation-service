package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.controller.dto.CreateRepeatDto;
import com.pipikonda.translationbot.controller.dto.RepeatAttemptDto;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.domain.TimePeriod;
import com.pipikonda.translationbot.domain.WordTranslation;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.BotUserRepository;
import com.pipikonda.translationbot.repository.RepeatRepository;
import com.pipikonda.translationbot.repository.WordTranslationRepository;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.GetTranslationPollDto;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
    private final TimePeriodService timePeriodService;
    private final WordTranslationRepository wordTranslationRepository;

    public Repeat createNewRepeat(CreateRepeatDto dto) {
        Instant nextRepeat = Instant.now().plus(RepeatAttemptService.baseRepeatInterval, RepeatAttemptService.repeatIntervalUnit);
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

    public List<Long> getUserWordTranslations(String userId) {
        return repeatRepository.findByUserId(userId);
    }

    @Scheduled(fixedDelay = 60000L)
    public void createRepeatAttempts() {
        log.info("Start createRepeatAttempts scheduler, server time is {}", LocalDateTime.now());
        Long repeatId;
        while ((repeatId = repeatRepository.getNextRepeat(Instant.now(), Instant.now().plusSeconds(60))) != null) {
            try {
                sendPoll(repeatId);
            } catch (Exception ex) {
                log.warn("Scheduled method got exception ", ex);
            }
        }
    }

    private void sendPoll(Long repeatId) throws TelegramApiException {
        Repeat repeat = repeatRepository.findById(repeatId)
                .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found repeat when expected"));
        BotUser botUser = botUserRepository.findById(Long.valueOf(repeat.getUserId()))
                .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found user by id " + repeat.getUserId() + " when expected"));
        List<TimePeriod> periods = timePeriodService.findByUserId(botUser.getId());
        boolean isTimeQuite = timePeriodService.checkTimeBetween(periods, LocalTime.now());
        if (isTimeQuite && checkRepeatLangs(repeat, botUser)) {
            SendMessage poll = getRepeat(botUser, repeat);
            translateBot.execute(poll);
        } else {
            log.info("Don't send poll to user. RepeatId - {} quite periods - {}", repeat.getId(), periods);
        }
    }

    private boolean checkRepeatLangs(Repeat repeat, BotUser botUser) {
        WordTranslation wordTranslation = wordTranslationRepository.findById(repeat.getId())
                .orElseThrow(() -> new BasicLogicException(ErrorCode.UNKNOWN_ERROR, "Not found wordTranslation when expected"));
        return wordTranslation.getTargetLang() == botUser.getTargetLang() &&
                wordTranslation.getSourceLang() == botUser.getSourceLang();
    }

    @Transactional
    public SendMessage getRepeat(BotUser botUser, Repeat repeat) {
        RepeatAttemptDto repeatAttempt = repeatAttemptService.createRepeatAttempt(repeat);
        SendMessage translationPollMessage = messageService.getTranslatePollKeyboard(GetTranslationPollDto.builder()
                .options(repeatAttempt.getValues())
                .askedValue(repeatAttempt.getAskedValue())
                .chatId(botUser.getChatId())
                .userLocale(Locale.getDefault())
                .repeatAttempt(repeatAttempt.getAttemptId())
                .build());
        log.info("Poll is {}", translationPollMessage);
        return translationPollMessage;
    }

    public Repeat findById(Long id) {
        return repeatRepository.findById(id)
                .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found repeat by id " + id));
    }
}
