package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.TimePeriod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TimePeriodServiceTest extends TestContainerBaseClass {

    @Autowired
    private TimePeriodService instance;

    @Test
    void checkTime_shouldReturnTrue_whenTimeInPeriod() {
        TimePeriod timePeriod = TimePeriod.builder()
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(16, 0))
                .build();
        assertThat(instance.checkTimeBetween(List.of(timePeriod), LocalTime.of(14, 0))).isTrue();
    }

    @Test
    void checkTime_shouldReturnFalse_whenTimeIsBeforePeriod() {
        TimePeriod timePeriod = TimePeriod.builder()
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(16, 0))
                .build();
        assertThat(instance.checkTimeBetween(List.of(timePeriod), LocalTime.of(11, 0))).isFalse();
    }

    @Test
    void checkTime_shouldReturnFalse_whenTimeIsAfterPeriod() {
        TimePeriod timePeriod = TimePeriod.builder()
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(16, 0))
                .build();
        assertThat(instance.checkTimeBetween(List.of(timePeriod), LocalTime.of(18, 0))).isFalse();
    }

    @Test
    void checkTime_shouldReturnFalse_whenTimeIsZero() {
        TimePeriod timePeriod = TimePeriod.builder()
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(16, 0))
                .build();
        assertThat(instance.checkTimeBetween(List.of(timePeriod), LocalTime.of(0, 0))).isFalse();
        assertThat(instance.checkTimeBetween(List.of(timePeriod), LocalTime.MIDNIGHT)).isFalse();
    }

    @Test
    void checkTime_shouldReturnTrue_whenTimeStartIsEqualTime() {
        TimePeriod timePeriod = TimePeriod.builder()
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(16, 0))
                .build();
        assertThat(instance.checkTimeBetween(List.of(timePeriod), LocalTime.of(12, 0))).isTrue();
    }

    @Test
    void checkTime_shouldReturnTrue_whenTimeEndIsEqualTime() {
        TimePeriod timePeriod = TimePeriod.builder()
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(16, 0))
                .build();
        assertThat(instance.checkTimeBetween(List.of(timePeriod), LocalTime.of(16, 0))).isTrue();
    }

    @Test
    void testCheckTime() {
        TimePeriod timePeriod1 = TimePeriod.builder()
                .startTime(LocalTime.of(22, 0))
                .endTime(LocalTime.MIDNIGHT)
                .build();
        TimePeriod timePeriod2 = TimePeriod.builder()
                .startTime(LocalTime.MIDNIGHT)
                .endTime(LocalTime.of(8, 0))
                .build();
        TimePeriod timePeriod3 = TimePeriod.builder()
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(16, 0))
                .build();

        assertThat(instance.checkTimeBetween(List.of(timePeriod1, timePeriod2, timePeriod3), LocalTime.of(0, 1))).isTrue();
        assertThat(instance.checkTimeBetween(List.of(timePeriod1, timePeriod2, timePeriod3), LocalTime.of(12, 30))).isTrue();
        assertThat(instance.checkTimeBetween(List.of(timePeriod1, timePeriod2, timePeriod3), LocalTime.of(21, 57))).isFalse();
        assertThat(instance.checkTimeBetween(List.of(timePeriod1, timePeriod2, timePeriod3), LocalTime.of(0, 0))).isTrue();
    }
}