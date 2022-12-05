package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Repeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RepeatRepository extends JpaRepository<Repeat, Long> {

    Optional<Repeat> findByUserIdAndWordTranslationId(String userId, Long wordTranslationId);

    @Query(value = "update repeats set next_repeat = :now " +
            "where id = (select id from repeats r where next_repeat < :now limit 1 for update) " +
            "returning id", nativeQuery = true)
    Long getNextRepeat(@Param("now")Instant now);
}
