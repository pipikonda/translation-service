package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.RepeatAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatAttemptRepository extends JpaRepository<RepeatAttempt, Long> {
}
