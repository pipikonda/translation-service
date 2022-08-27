package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.domain.TranslationInfo;
import com.pipikonda.translationbot.domain.TranslationType;
import com.pipikonda.translationbot.repository.TranslationInfoRepository;
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
    private final TranslationInfoRepository translationInfoRepository;

    @Transactional
    public Long saveTranslations(Map<Lang, List<String>> translations, String userId) {
        Long translationId = translationInfoRepository.save(TranslationInfo.builder()
                .userId(userId)
                .type(TranslationType.WORD)
                .build()).getId();
        List<Translation> translationList = translations.entrySet().stream()
                .flatMap(e -> e.getValue().stream()
                        .map(v -> Translation.builder()
                                .lang(e.getKey())
                                .value(v)
                                .translationId(translationId)
                                .build()))
                .toList();
        translationRepository.saveAll(translationList);
        return translationId;
    }

}
