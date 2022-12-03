package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.WordTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordTranslationRepository extends JpaRepository<WordTranslation, Long> {

    List<WordTranslation> findBySourceTranslationIdAndSourceLangAndTargetLang(Long translationId, Lang sourceLang, Lang targetLang);

    @Query(value = "select exists (select 1 from word_translations where source_translation_id = :sourceTranslationId and " +
            "target_translation_id = :targetTranslationId and source_lang = :sourceLang and target_lang = :targetLang and " +
            "user_id = :userId)", nativeQuery = true)
    boolean checkCustomTranslation(@Param("sourceTranslationId") Long sourceId,
                                                     @Param("targetTranslationId") Long targetId,
                                                     @Param("sourceLang") Lang sourceLang,
                                                     @Param("targetLang") Lang targetLang,
                                                     @Param("userId") String userId);
}
