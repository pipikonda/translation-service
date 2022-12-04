package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.BotUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoField;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BotUserRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private BotUserRepository instance;

    @BeforeEach
    @AfterEach
    void clearDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        BotUser botUser = instance.save(BotUser.builder()
                .chatId(123L)
                .userState(BotUser.UserState.LEFT)
                .subscribed(false)
                .lastSubscribedTime(Instant.now().with(ChronoField.NANO_OF_SECOND, 0))
                .lastUnsubscribedTime(Instant.now().with(ChronoField.NANO_OF_SECOND, 0))
                .build());

        assertThat(botUser.getId()).isNotNull();
        assertThat(instance.findById(botUser.getId())).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(botUser));
    }

    @Test
    void testFindByChatId() {
        BotUser botUser = instance.save(BotUser.builder()
                .chatId(123L)
                .userState(BotUser.UserState.LEFT)
                .subscribed(false)
                .lastSubscribedTime(Instant.now().with(ChronoField.NANO_OF_SECOND, 0))
                .lastUnsubscribedTime(Instant.now().with(ChronoField.NANO_OF_SECOND, 0))
                .build());

        assertThat(instance.findByChatId(botUser.getChatId())).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(botUser));
    }
}
