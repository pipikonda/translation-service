package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TranslationRepository extends JpaRepository<Translation, Long> {

    @Query("select t.wordId from Translation t where t.value = :value and t.lang = :lang " +
            "and (t.userId = :userId or t.userId = '-1')")
    Set<Long> findUserWordsId(@Param("value") String value, @Param("lang") Lang lang, @Param("userId") String userId);

    @Query("select t.value from Translation t where t.wordId in :wordsId and t.lang = :lang " +
            "and (t.userId = :userId or t.userId = '-1')")
    List<String> findUserTranslateByWordIdAndLang(@Param("wordsId") Set<Long> wordsId, @Param("lang") Lang lang, @Param("userId") String userId);

}
