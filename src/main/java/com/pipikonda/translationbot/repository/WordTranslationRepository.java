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

    @Query("select wt from WordTranslation wt where wt.id = :id and (wt.userId = :userId or wt.userId is null)")
    Optional<WordTranslation> findByIdAndUserId(@Param("id") Long id, @Param("userId") String userId);

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
            "wt.targetTranslationId <> :correctAnswerId and (wt.userId is null or wt.userId = :userId)")
    List<Long> getFakeAnswersId(@Param("targetLang") Lang targetLang,
                                @Param("correctAnswerId") Long correctAnswerId,
                                @Param("userId") String userId);

    @Query(value = "select * from word_translations wt where wt.source_lang = :sourceLang and " +
            "wt.target_lang = :targetLang and " +
            "wt.user_id is null and " +
            "wt.id not in :excludedIds " +
            "order by RANDOM() limit 1",
            nativeQuery = true)
    Optional<WordTranslation> getRandomWordExcludeId(@Param("excludedIds") List<Long> excludedIds,
                                                     @Param("sourceLang") String sourceLang,
                                                     @Param("targetLang") String targetLang);

    default Optional<WordTranslation> getRandomWord(List<Long> excludedIds, Lang sourceLang, Lang targetLang) {
        if (excludedIds.isEmpty()) {
            excludedIds = List.of(-1L);
        }
        return getRandomWordExcludeId(excludedIds, sourceLang.name(), targetLang.name());
    }
}
