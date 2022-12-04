package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByRepeatAttemptIdAndTranslationValueIdAndIsCorrectIsTrue(Long repeatAttemptId, Long translationValueId);
}
