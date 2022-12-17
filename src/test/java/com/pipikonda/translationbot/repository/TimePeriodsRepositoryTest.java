package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.TimePeriod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class TimePeriodsRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private TimePeriodRepository instance;

    @BeforeEach
    @AfterEach
    void clearDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        TimePeriod timePeriod = instance.save(TimePeriod.builder()
                .userId(2L)
                .startTime(LocalTime.now().withNano(0))
                .endTime(LocalTime.now().plusHours(2).withNano(0))
                .build());
        assertThat(timePeriod.getId()).isNotNull();
        assertThat(instance.findById(timePeriod.getId())).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(timePeriod));
    }

    @Test
    void testFindByUserId() {
        TimePeriod timePeriod = instance.save(TimePeriod.builder()
                .userId(2L)
                .startTime(LocalTime.now().withNano(0))
                .endTime(LocalTime.now().plusHours(2).withNano(0))
                .build());

        assertThat(instance.findByUserId(2L)).isEqualTo(List.of(timePeriod));
        assertThat(instance.findByUserId(22L)).isEmpty();
    }

    @Test
    void isPeriodPresent_shouldReturnFalse_whenTableIsEmpty() {
        assertThat(instance.isPeriodPresent(1L, LocalTime.of(12, 0),
                LocalTime.of(12, 0).plusHours(2))).isFalse();
    }

    @Test
    void isPeriodPresent_shouldReturnFalse_whenUserIdIsNotEqual() {
        TimePeriod timePeriod = instance.save(TimePeriod.builder()
                .userId(2L)
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(12, 0).plusHours(2))
                .build());
        assertThat(instance.isPeriodPresent(1L, LocalTime.of(12, 0),
                LocalTime.of(12, 0).plusHours(2))).isFalse();
    }

    @Test
    void isPeriodPresent_shouldReturnFalse_whenStartBetweenAnotherPeriod() {
        TimePeriod timePeriod = instance.save(TimePeriod.builder()
                .userId(2L)
                .startTime(LocalTime.of(12, 0).minusHours(2))
                .endTime(LocalTime.of(12, 0).plusHours(2))
                .build());
        assertThat(instance.isPeriodPresent(2L, LocalTime.of(12, 0),
                LocalTime.of(12, 0).plusHours(4))).isTrue();
    }

    @Test
    void isPeriodPresent_shouldReturnFalse_whenEndBetweenAnotherPeriod() {
        TimePeriod timePeriod = instance.save(TimePeriod.builder()
                .userId(2L)
                .startTime(LocalTime.of(12, 0).minusHours(2))
                .endTime(LocalTime.of(12, 0).plusHours(2))
                .build());
        assertThat(instance.isPeriodPresent(2L, LocalTime.of(12, 0).minusHours(4),
                LocalTime.of(12, 0))).isTrue();
    }

    @Test
    void isPeriodPresent_shouldReturnFalse_whenInputPeriodIsMore() {
        TimePeriod timePeriod = instance.save(TimePeriod.builder()
                .userId(2L)
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .build());
        assertThat(instance.isPeriodPresent(2L, LocalTime.of(10, 0),
                LocalTime.of(16, 0))).isTrue();
    }

    @Test
    void testIsPeriodPresent() {
        TimePeriod timePeriod = instance.save(TimePeriod.builder()
                .userId(2L)
                .startTime(LocalTime.of(12, 0).minusHours(2))
                .endTime(LocalTime.of(12, 0).plusHours(2))
                .build());
        assertThat(instance.isPeriodPresent(2L, LocalTime.of(12, 0).minusHours(5),
                LocalTime.of(12, 0).minusHours(4))).isFalse();
    }

    @Test
    void testIsPeriodPresent_shouldReturnFalse_whenStartAndEndAreEqual() {
        TimePeriod timePeriod = instance.save(TimePeriod.builder()
                .userId(2L)
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(12, 0))
                .build());
        assertThat(instance.isPeriodPresent(2L,
                LocalTime.now().minusHours(1), LocalTime.now().plusHours(1))).isFalse();
    }
}
