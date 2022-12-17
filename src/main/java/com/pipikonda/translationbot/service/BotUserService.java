package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class BotUserService {

    private final BotUserRepository botUserRepository;
    private final TimePeriodService timePeriodService;

    @Transactional
    public BotUser getBotUserByChatId(Long chatId) {
        try {
            BotUser botUser = botUserRepository.findByChatId(chatId)
                    .orElseGet(() -> botUserRepository.save(BotUser.builder()
                            .chatId(chatId)
                            .userState(BotUser.UserState.ACTIVE)
                            .lastStateChanged(Instant.now())
                            .lastSubscribedTime(Instant.now())
                            .sourceLang(Lang.EN)
                            .targetLang(Lang.RU)
                            .build()));
            timePeriodService.add(botUser.getId(), LocalTime.of(22, 0), LocalTime.MIDNIGHT);
            timePeriodService.add(botUser.getId(), LocalTime.MIDNIGHT, LocalTime.of(8, 0));
            return botUser;
        } catch (ConstraintViolationException e) {
            return botUserRepository.findByChatId(chatId)
                    .orElseThrow(() -> new BasicLogicException(ErrorCode.UNKNOWN_ERROR, "Not found user when expected"));
        }
    }

    public BotUser save(BotUser botUser) {
        return botUserRepository.save(botUser);
    }

    public void changeLangs(BotUser botUser, Lang sourceLang, Lang targetLang) {
        if (sourceLang == targetLang) {
            throw new BasicLogicException(ErrorCode.BAD_REQUEST, "Source and target langs are equal");
        }
        botUserRepository.save(botUser.toBuilder()
                .targetLang(targetLang)
                .sourceLang(sourceLang)
                .build());
    }
}
