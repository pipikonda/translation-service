package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.domain.Word;
import com.pipikonda.translationbot.dto.WordCreateDto;
import com.pipikonda.translationbot.dto.WordResponseDto;
import com.pipikonda.translationbot.dto.WordTranslateDto;
import com.pipikonda.translationbot.repository.TranslationInfoRepository;
import com.pipikonda.translationbot.repository.TranslationRepository;
import com.pipikonda.translationbot.repository.WordRepository;
import com.pipikonda.translationbot.service.http.MyMemoryTranslateClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final TranslationService translationService;
    private final MyMemoryTranslateClient myMemoryTranslateClient;
    private final TranslationRepository translationRepository;
    private final TranslationInfoRepository translationInfoRepository;

    @Transactional
    public Word create(WordCreateDto dto) {
        return wordRepository.save(Word.builder()
                .translationId(translationService.saveTranslations(dto.getTranslations(), dto.getUserId()))
                .build());
    }

    public WordResponseDto translate(WordTranslateDto dto) {
        dto = dto.toBuilder()
                .value(dto.getValue().toLowerCase())
                .build();
        List<Translation> translations = translationRepository.findByValueAndLang(dto.getValue(), dto.getSourceLang());
        List<Long> translationsInfo = translations.stream()
                .map(Translation::getTranslationId)
                .distinct()
                .toList();
        Set<Long> translationsInfoId = translationInfoRepository.findUserTranslations(translationsInfo, dto.getUserId());
        List<Long> targetTranslationInfoIdList = translations.stream()
                .map(Translation::getTranslationId)
                .filter(translationsInfoId::contains)
                .toList();

        List<String> translationValues = translationRepository.findByLangAndTranslationIdIn(dto.getTargetLang(), targetTranslationInfoIdList)
                .stream()
                .map(Translation::getValue)
                .toList();

        if (!translationValues.isEmpty()) {
            return WordResponseDto.builder()
                    .translations(translationValues)
                    .targetLang(dto.getTargetLang())
                    .sourceLang(dto.getSourceLang())
                    .inputValue(dto.getValue())
                    .build();
        }

        String externalTranslate = myMemoryTranslateClient.getTranslation(dto.getSourceLang(), dto.getTargetLang(), dto.getValue());
        Word word = create(WordCreateDto.builder()
                .userId("-1")
                .translations(Map.of(dto.getSourceLang(), List.of(dto.getValue()), dto.getTargetLang(), List.of(externalTranslate)))
                .build());
        return WordResponseDto.builder()
                .id(word.getId())
                .targetLang(dto.getTargetLang())
                .sourceLang(dto.getSourceLang())
                .inputValue(dto.getValue())
                .translations(List.of(externalTranslate))
                .build();
    }

    private void checkTranslateExists(String textValue, Lang sourceLang) {
        //find by value and lang
    }

}
