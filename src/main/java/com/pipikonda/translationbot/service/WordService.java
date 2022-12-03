package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.controller.dto.CreateCustomTranslateDto;
import com.pipikonda.translationbot.controller.dto.CreateWordDto;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.domain.WordTranslation;
import com.pipikonda.translationbot.repository.TranslationRepository;
import com.pipikonda.translationbot.repository.WordTranslationRepository;
import com.pipikonda.translationbot.service.http.MyMemoryTranslateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordService {

    private final TranslationService translationService;
    private final MyMemoryTranslateClient myMemoryTranslateClient;
    private final TranslationRepository translationRepository;
    private final WordTranslationRepository wordTranslationRepository;

    public List<Translation> getTranslations(CreateWordDto dto) {
        Translation sourceTranslation = translationService.getTranslationByValue(dto.getWord());
        log.info("Try to search word {} source lang {} to target lang {}", dto.getWord(), dto.getSourceLang(), dto.getTargetLang());
        List<Long> targetTranslationsId =
                wordTranslationRepository.findBySourceTranslationIdAndSourceLangAndTargetLang(
                                sourceTranslation.getId(), dto.getSourceLang(), dto.getTargetLang()
                        ).stream()
                        .map(WordTranslation::getTargetTranslationId)
                        .toList();
        if (targetTranslationsId.isEmpty()) {
            log.debug("targetTranslationsId are empty");
            targetTranslationsId = translate(dto.getSourceLang(), dto.getTargetLang(), dto.getWord(), sourceTranslation.getId());
        }
        log.info("targetTranslationsId ==> {}", targetTranslationsId);
        return translationRepository.findByIdIn(targetTranslationsId);
    }

    private List<Long> translate(Lang sourceLang, Lang targetLang, String value, Long sourceTranslationId) {
        List<String> translatedValues = myMemoryTranslateClient.getTranslation(sourceLang, targetLang, value)
                .stream()
                .map(String::toLowerCase)
                .toList();
        log.info("Translate {} from {} to {} is {}", value, sourceLang, targetLang, translatedValues);
        return translatedValues
                .stream()
                .map(translationService::getTranslationByValue)
                .map(e -> wordTranslationRepository.save(WordTranslation.builder()
                        .sourceTranslationId(sourceTranslationId)
                        .targetTranslationId(e.getId())
                        .targetLang(targetLang)
                        .sourceLang(sourceLang)
                        .build()))
                .map(WordTranslation::getTargetTranslationId)
                .toList();
    }

    public void createCustomTranslate(CreateCustomTranslateDto dto) {
        Translation sourceTranslationId = translationService.getTranslationByValue(dto.getSourceValue());
        Translation targetTranslationId = translationService.getTranslationByValue(dto.getTargetValue());
        //check if present
        wordTranslationRepository.save(WordTranslation.builder()
                .sourceLang(dto.getSourceLang())
                .targetLang(dto.getTargetLang())
                .sourceTranslationId(sourceTranslationId.getId())
                .targetTranslationId(targetTranslationId.getId())
                .userId(dto.getUserId())
                .build());
    }
}
