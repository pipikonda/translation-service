package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Word;
import com.pipikonda.translationbot.repository.RepeatRepository;
import com.pipikonda.translationbot.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepeatService {

    private final RepeatRepository repeatRepository;
    private final TranslationRepository translationRepository;

    public void createNewRepeat(Word word, Lang targetLang, Lang sourceLang) {

    }
}
