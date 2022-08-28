package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;

    @Transactional
    public void saveTranslations(Map<Lang, List<String>> translations, Long wordId) {
        List<Translation> translationList = translations.entrySet().stream()
                .flatMap(e -> e.getValue().stream()
                        .map(v -> Translation.builder()
                                .lang(e.getKey())
                                .textValue(v)
                                .wordId(wordId)
                                .build()))
                .toList();
        translationRepository.saveAll(translationList);
    }

}
