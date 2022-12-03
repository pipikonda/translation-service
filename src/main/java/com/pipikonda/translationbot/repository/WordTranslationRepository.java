package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.WordTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordTranslationRepository extends JpaRepository<WordTranslation, Long> {

    List<WordTranslation> findBySourceTranslationIdAndSourceLangAndTargetLang(Long translationId, Lang sourceLang, Lang targetLang);

}
