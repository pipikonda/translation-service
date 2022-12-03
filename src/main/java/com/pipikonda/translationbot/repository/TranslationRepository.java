package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {

    Optional<Translation> findByTextValue(String textValue);

    List<Translation> findByIdIn(List<Long> ids);
}