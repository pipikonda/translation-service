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
    void testFindByRepeatAttemptId() {
        Answer answer1 = instance.save(Answer.builder()
                .translationValueId(2222L)
                .isCorrect(false)
                .repeatAttemptId(14L)
                .build());
        Answer answer2 = instance.save(Answer.builder()
                .translationValueId(2227L)
                .isCorrect(true)
                .repeatAttemptId(14L)
                .build());
        Answer answer3 = instance.save(Answer.builder()
                .translationValueId(2216L)
                .isCorrect(false)
                .repeatAttemptId(15L)
                .build());
        assertThat(instance.findByRepeatAttemptId(14L)).containsOnly(answer2, answer1);
    }

    @Test
    void testFindByRepeatAttemptId_shouldReturnEmpty_whenTableIsEmpty() {
        assertThat(instance.findByRepeatAttemptId(14L)).isEmpty();
    }

    @Test
    void testFindByRepeatAttemptId_shouldReturnEmpty_whenAttemptIdIsNotExists() {
        Answer answer3 = instance.save(Answer.builder()
                .translationValueId(2216L)
                .isCorrect(false)
                .repeatAttemptId(15L)
                .build());
        assertThat(instance.findByRepeatAttemptId(14L)).isEmpty();
    }
}
