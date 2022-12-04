package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Answer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AnswerRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private AnswerRepository instance;

    @BeforeEach
    @AfterEach
    void clearDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        Answer answer = instance.save(Answer.builder()
                        .translationValueId(2222L)
                        .isCorrect(false)
                        .repeatAttemptId(14L)
                .build());
        assertThat(answer.getId()).isNotNull();
    }

    @Test
    void findByRepeatAttemptIdAndTranslationValueIdAndCorrectIsTrue_shouldReturnEmpty_whenTableEmpty() {
        assertThat(instance.findByRepeatAttemptIdAndTranslationValueIdAndIsCorrectIsTrue(23L, 11L)).isEmpty();
    }

    @Test
    void findByRepeatAttemptIdAndTranslationValueIdAndCorrectIsTrue_shouldReturnEmpty_whenCorrectIsFalse() {
        Answer answer = instance.save(Answer.builder()
                .translationValueId(2222L)
                .isCorrect(false)
                .repeatAttemptId(14L)
                .build());
        assertThat(instance.findByRepeatAttemptIdAndTranslationValueIdAndIsCorrectIsTrue(14L, 2222L)).isEmpty();
    }

    @Test
    void findByRepeatAttemptIdAndTranslationValueIdAndCorrectIsTrue() {
        Answer answer = instance.save(Answer.builder()
                .translationValueId(2222L)
                .isCorrect(true)
                .repeatAttemptId(14L)
                .build());
        assertThat(instance.findByRepeatAttemptIdAndTranslationValueIdAndIsCorrectIsTrue(14L, 2222L)).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(answer));
    }
}
