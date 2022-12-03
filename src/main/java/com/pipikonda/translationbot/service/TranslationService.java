package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;

    @Transactional
    public Translation getTranslationByValue(String value) {
        try {
            return translationRepository.findByTextValue(value)
                    .orElseGet(() -> translationRepository.save(Translation.builder()
                            .textValue(value)
                            .build()));
        } catch (ConstraintViolationException e) {
            return translationRepository.findByTextValue(value)
                    .orElseThrow(() -> new BasicLogicException(ErrorCode.UNKNOWN_ERROR, "Not found translation value when expected"));
        }
    }

}
