package com.pipikonda.translationbot.telegram.service;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BotUserService {

    private final BotUserRepository botUserRepository;

    public BotUser getBotUserByChatId(Long chatId) {
        try {
            return botUserRepository.findByChatId(chatId)
                    .orElseGet(() -> botUserRepository.save(BotUser.builder()
                            .chatId(chatId)
                            .lastSubscribedTime(Instant.now())
                            .build()));
        } catch (ConstraintViolationException e) {
            return botUserRepository.findByChatId(chatId)
                    .orElseThrow(() -> new BasicLogicException(ErrorCode.UNKNOWN_ERROR, "Not found user when expected"));
        }
    }

    public BotUser save(BotUser botUser) {
        return botUserRepository.save(botUser);
    }
}
