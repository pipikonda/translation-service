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

    @Test
    void testCountByRepeatIdAndIsSuccessIsTrue_shouldReturnZero_whenTableIsEmpty() {
        assertThat(instance.countByRepeatIdAndIsSuccessIsTrue(2L)).isZero();
    }

    @Test
    void testCountByRepeatIdAndIsSuccessIsTrue_shouldReturnZero_whenRepeatIdIsNotPresent() {
        instance.save(RepeatAttempt.builder()
                .attemptNumber(27)
                .isSuccess(true)
                .repeatId(22L)
                .build());
        assertThat(instance.countByRepeatIdAndIsSuccessIsTrue(4L)).isZero();
    }

    @Test
    void testCountByRepeatIdAndIsSuccessIsTrue_shouldReturnZero_whenRepeatAttemptIsFalse() {
        instance.save(RepeatAttempt.builder()
                .attemptNumber(27)
                .isSuccess(false)
                .repeatId(22L)
                .build());
        assertThat(instance.countByRepeatIdAndIsSuccessIsTrue(22L)).isZero();
    }

    @Test
    void testCountByRepeatIdAndIsSuccessIsTrue() {
        instance.save(RepeatAttempt.builder()
                .attemptNumber(27)
                .isSuccess(true)
                .repeatId(22L)
                .build());
        instance.save(RepeatAttempt.builder()
                .attemptNumber(26)
                .isSuccess(true)
                .repeatId(22L)
                .build());
        instance.save(RepeatAttempt.builder()
                .attemptNumber(25)
                .isSuccess(false)
                .repeatId(22L)
                .build());
        instance.save(RepeatAttempt.builder()
                .attemptNumber(20)
                .isSuccess(false)
                .repeatId(22L)
                .build());
        assertThat(instance.countByRepeatIdAndIsSuccessIsTrue(22L)).isEqualTo(2);
    }

    @Test
    void testFindMaxAttemptNumberByRepeatId_shouldReturnZero_whenTableIsEmpty() {
        assertThat(instance.findMaxAttemptNumberByRepeatId(4L)).isNull();
    }

    @Test
    void testFindMaxAttemptNumberByRepeatId_shouldReturnZero_whenRepeatIdIsNotPresent() {
        RepeatAttempt attempt = instance.save(RepeatAttempt.builder()
                .repeatId(2L)
                .build());
        assertThat(instance.findMaxAttemptNumberByRepeatId(4L)).isNull();
    }

    @Test
    void testFindMaxAttemptNumberByRepeatId_shouldReturnZero_whenAttemptNumberIsNull() {
        RepeatAttempt attempt = instance.save(RepeatAttempt.builder()
                .repeatId(4L)
                .build());
        assertThat(instance.findMaxAttemptNumberByRepeatId(4L)).isNull();
    }

    @Test
    void testFindMaxAttemptNumberByRepeatId() {
        RepeatAttempt attempt1 = instance.save(RepeatAttempt.builder()
                .repeatId(4L)
                .attemptNumber(-5)
                .build());
        RepeatAttempt attempt2 = instance.save(RepeatAttempt.builder()
                .repeatId(4L)
                .attemptNumber(19)
                .build());
        RepeatAttempt attempt3 = instance.save(RepeatAttempt.builder()
                .repeatId(4L)
                .attemptNumber(23)
                .build());
        RepeatAttempt attempt4 = instance.save(RepeatAttempt.builder()
                .repeatId(4L)
                .attemptNumber(45)
                .build());
        assertThat(instance.findMaxAttemptNumberByRepeatId(4L)).isEqualTo(45);
    }
}
