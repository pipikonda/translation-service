package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.RepeatAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RepeatAttemptRepository extends JpaRepository<RepeatAttempt, Long> {

    long countByRepeatIdAndIsSuccessIsTrue(Long repeatId);

    @Query(value = "select max(attempt_number) from repeat_attempts where repeat_id = :repeatId", nativeQuery = true)
    Integer findMaxAttemptNumberByRepeatId(@Param("repeatId") Long repeatId);

    @Query(value = "select * from repeat_attempts where repeat_id = :repeatId order by id desc limit 1", nativeQuery = true)
    Optional<RepeatAttempt> findLastRepeatAttempt(@Param("repeatId") Long repeatId);

}
