package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.controller.dto.CreateCustomTranslateDto;
import com.pipikonda.translationbot.controller.dto.CreateWordDto;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.domain.WordTranslation;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.TranslationRepository;
import com.pipikonda.translationbot.repository.WordTranslationRepository;
import com.pipikonda.translationbot.service.http.MyMemoryTranslateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<WordTranslation> translations =
                wordTranslationRepository.findBySourceTranslationIdAndSourceLangAndTargetLang(
                        sourceTranslation.getId(), dto.getSourceLang(), dto.getTargetLang()
                );
        List<Long> targetTranslationsId = translations.stream()
                .filter(e -> e.getUserId() == null)
                .map(WordTranslation::getTargetTranslationId)
                .collect(Collectors.toList());
        if (targetTranslationsId.isEmpty()) {
            log.debug("targetTranslationsId are empty");
            targetTranslationsId = translate(dto.getSourceLang(), dto.getTargetLang(), dto.getWord(), sourceTranslation.getId());
        }
        log.info("targetTranslationsId ==> {}", targetTranslationsId);

        List<Long> resultTranslations = targetTranslationsId;
        Optional.ofNullable(dto.getUserId())
                .ifPresent(e -> {
                    List<Long> userTranslations = translations.stream()
                            .filter(v -> e.equals(v.getUserId()))
                            .map(WordTranslation::getTargetTranslationId)
                            .toList();

                    resultTranslations.addAll(userTranslations);
                });

        return translationRepository.findAllById(resultTranslations);
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
                .collect(Collectors.toList());
    }

    public void createCustomTranslate(CreateCustomTranslateDto dto) {
        Long sourceTranslationId = translationService.getTranslationByValue(dto.getSourceValue()).getId();
        Long targetTranslationId = translationService.getTranslationByValue(dto.getTargetValue()).getId();
        Optional<WordTranslation> translation =
                wordTranslationRepository.checkCustomTranslation(sourceTranslationId, targetTranslationId,
                        dto.getSourceLang(), dto.getTargetLang(), dto.getUserId());
        if (translation.isPresent()) {
            throw new BasicLogicException(ErrorCode.BAD_REQUEST, "Such translation is already present");
        }
        wordTranslationRepository.save(WordTranslation.builder()
                .sourceLang(dto.getSourceLang())
                .targetLang(dto.getTargetLang())
                .sourceTranslationId(sourceTranslationId)
                .targetTranslationId(targetTranslationId)
                .userId(dto.getUserId())
                .build());
    }
}
