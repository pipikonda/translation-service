package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranslationRepository extends JpaRepository<Translation, Long> {

    List<Translation> findByValueAndLang(String value, Lang lang);

    List<Translation> findByLangAndTranslationIdIn(Lang lang, List<Long> translationIdList);
}
