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

}
