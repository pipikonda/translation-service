package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotUserRepository extends JpaRepository<BotUser, Long> {

    Optional<BotUser> findByChatId(Long chatId);
}
