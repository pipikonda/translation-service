package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.RepeatAttempt;
import com.pipikonda.translationbot.domain.RepeatType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RepeatAttemptRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private RepeatAttemptRepository instance;

    @BeforeEach
    @AfterEach
    void clearDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        RepeatAttempt repeatAttempt = instance.save(RepeatAttempt.builder()
                        .isSuccess(false)
                        .attemptNumber(2)
                        .repeatId(1L)
                        .repeatType(RepeatType.TRANSLATE_TEST)
                        .attemptTime(Instant.now())
                .build());

        assertThat(repeatAttempt.getId()).isNotNull();
    }
}
