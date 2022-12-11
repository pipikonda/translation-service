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

    @Test
    void findByUserIdAndWordTranslationId() {
        Repeat repeat = instance.save(Repeat.builder()
                .userId("some user id")
                .lastRepeat(Instant.now().with(ChronoField.NANO_OF_SECOND, 0))
                .wordTranslationId(23L)
                .build());

        assertThat(instance.findByUserIdAndWordTranslationId("some user id", 23L)).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(repeat));
        assertThat(instance.findByUserIdAndWordTranslationId("another user id", 23L)).isEmpty();
    }

    @Test
    void getNextRepeat_shouldReturnEmpty_whenRepeatIsNotPresent() {
        assertThat(instance.getNextRepeat(Instant.now())).isNull();
    }

    @Test
    void getNextRepeat() {
        Repeat repeat = instance.save(Repeat.builder()
                .userId("some user id")
                .nextRepeat(Instant.now().minusSeconds(60))
                .wordTranslationId(23L)
                .build());
        assertThat(instance.getNextRepeat(Instant.now())).isEqualTo(repeat.getId());
    }

    @Test
    void testFindByUserId() {
        Repeat repeat1 = instance.save(Repeat.builder()
                .userId("some user id")
                .nextRepeat(Instant.now().minusSeconds(60))
                .wordTranslationId(23L)
                .build());
        Repeat repeat2 = instance.save(Repeat.builder()
                .userId("some user id")
                .nextRepeat(Instant.now().minusSeconds(60))
                .wordTranslationId(25L)
                .build());
        assertThat(instance.findByUserId("some user id"))
                .containsOnly(repeat2.getWordTranslationId(), repeat1.getWordTranslationId());
    }
}
