package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByRepeatAttemptId(Long repeatAttemptId);
}
