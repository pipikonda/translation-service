package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
}
