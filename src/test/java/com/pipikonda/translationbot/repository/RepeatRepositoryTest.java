package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Repeat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoField;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RepeatRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private RepeatRepository instance;

    @BeforeEach
    @AfterEach
    void clearDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        Repeat repeat = instance.save(Repeat.builder()
                .userId("some user id")
                .lastRepeat(Instant.now().with(ChronoField.NANO_OF_SECOND, 0))
                .wordTranslationId(23L)
                .build());
        assertThat(repeat.getId()).isNotNull();
    }
}
