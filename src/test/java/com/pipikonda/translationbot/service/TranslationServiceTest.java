package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.repository.TranslationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TranslationServiceTest extends TestContainerBaseClass {

    @Autowired
    private TranslationService instance;

    @Autowired
    private TranslationRepository translationRepository;

    @BeforeEach
    @AfterEach
    void clearDb() {
        translationRepository.deleteAll();
    }

    @Test
    void testSaveTranslations() {
        Map<Lang, List<String>> translations = Map.of(
                Lang.RU, List.of("Еда"),
                Lang.EN, List.of("food", "meal"));

        instance.saveTranslations(translations, 2223L);

        List<Translation> translationResult = translationRepository.findAll();

        assertThat(translationResult.size()).isEqualTo(3);

        assertThat(translationResult.stream()
                .filter(e -> Lang.EN == e.getLang())
                .map(Translation::getValue)
                .toList()).containsOnly("food", "meal");

        assertThat(translationResult.stream()
                .filter(e -> Lang.RU == e.getLang())
                .map(Translation::getValue)
                .toList()).containsOnly("Еда");
    }
}