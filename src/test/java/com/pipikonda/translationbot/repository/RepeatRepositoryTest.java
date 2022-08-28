package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Repeat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

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
                .wordId(2L)
                .sourceLang(Lang.EN)
                .targetLang(Lang.RU)
                .lastRepeat(Instant.MIN)
                .lastRepeat(Instant.now())
                .build());
        assertThat(repeat.getId()).isNotNull();
    }
}
