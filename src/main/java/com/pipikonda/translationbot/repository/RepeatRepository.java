package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Repeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepeatRepository extends JpaRepository<Repeat, Long> {

    Optional<Repeat> findByUserIdAndWordTranslationId(String userId, Long wordTranslationId);
}
