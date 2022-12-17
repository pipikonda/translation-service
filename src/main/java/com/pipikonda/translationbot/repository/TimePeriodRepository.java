package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.TimePeriod;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface TimePeriodRepository extends CrudRepository<TimePeriod, Long> {

    List<TimePeriod> findByUserId(Long userId);

    @Query(value = "select exists (select 1 from time_periods where user_id = :userId and " +
            "(:start between start_time and end_time or :end between start_time and end_time or " +
            "(start_time >= :start and end_time <= :end)))", nativeQuery = true)
    boolean isPeriodPresent(@Param("userId") Long userId, @Param("start") LocalTime start, @Param("end") LocalTime end);
}
