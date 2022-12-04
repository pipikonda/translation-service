package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.WordTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordTranslationRepository extends JpaRepository<WordTranslation, Long> {

    List<WordTranslation> findBySourceTranslationIdAndSourceLangAndTargetLang(Long translationId, Lang sourceLang, Lang targetLang);

    @Query("select wt from WordTranslation wt where " +
            "wt.sourceTranslationId = :sourceTranslationId and wt.targetTranslationId = :targetTranslationId and " +
            "wt.targetLang = :targetLang and wt.sourceLang = :sourceLang and " +
            "wt.userId = :userId")
    Optional<WordTranslation> checkCustomTranslation(@Param("sourceTranslationId") Long sourceId,
                                    @Param("targetTranslationId") Long targetId,
                                    @Param("sourceLang") Lang sourceLang,
                                    @Param("targetLang") Lang targetLang,
                                    @Param("userId") String userId);

    @Query("select wt.targetTranslationId from WordTranslation wt where wt.targetLang = :targetLang and " +
            "wt.targetTranslationId <> :correctAnswerId")
    List<Long> getFakeAnswersId(@Param("targetLang") Lang targetLang,
                                @Param("correctAnswerId") Long correctAnswerId);
}
