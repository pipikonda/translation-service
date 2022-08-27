package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.TranslationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Set;

public interface TranslationInfoRepository extends JpaRepository<TranslationInfo, Long> {

    @Query("select ti.id from TranslationInfo ti where ti.id in :ids and " +
            "ti.type = 'WORD' and (ti.userId = :userId or ti.userId = '-1')")
    Set<Long> findUserTranslations(Collection<Long> ids, String userId);
}
