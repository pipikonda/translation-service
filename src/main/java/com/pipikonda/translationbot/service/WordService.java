package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Word;
import com.pipikonda.translationbot.dto.WordCreateDto;
import com.pipikonda.translationbot.dto.WordResponseDto;
import com.pipikonda.translationbot.dto.WordTranslateDto;
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

    @Transactional
    public Word create(WordCreateDto dto) {
        Word word = wordRepository.save(Word.builder()
                .build());
        translationService.saveTranslations(dto.getTranslations(), word.getId());
        return word;
    }

    public WordResponseDto getTranslation(WordTranslateDto dto) {
        dto = dto.toBuilder()
                .value(dto.getValue().toLowerCase())
                .build();
        Set<Long> wordsId = translationRepository.findUserWordsId(dto.getValue(), dto.getSourceLang(), dto.getUserId());
        List<String> translationValues = translationRepository.findUserTranslateByWordIdAndLang(wordsId, dto.getTargetLang(), dto.getUserId());

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
}
