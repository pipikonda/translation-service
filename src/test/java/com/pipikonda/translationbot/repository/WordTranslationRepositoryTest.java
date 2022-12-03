package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.WordTranslation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WordTranslationRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private WordTranslationRepository instance;

    @BeforeEach
    @AfterEach
    void clearDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(23L)
                .sourceLang(Lang.RU)
                .build());

        assertThat(wordTranslation.getId()).isNotNull();
        assertThat(instance.findById(wordTranslation.getId())).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(wordTranslation));
    }

    @Test
    void testCheckCustomTranslation_shouldReturnFalse_whenTranslationIsNotPresent() {
        assertThat(instance.checkCustomTranslation(22L, 45L, Lang.RU, Lang.EN, "qwe")).isEmpty();
    }

    @Test
    void testCheckCustomTranslation_shouldReturnFalse_whenUserIdIsNull() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(22L)
                .targetTranslationId(45L)
                .sourceLang(Lang.RU)
                .targetLang(Lang.EN)
                .build());

        assertThat(instance.checkCustomTranslation(22L, 45L, Lang.RU, Lang.EN, "qwe")).isEmpty();
    }

    @Test
    void testCheckCustomTranslation_shouldReturnFalse_whenUserIdIsNotEqualInput() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(22L)
                .targetTranslationId(45L)
                .sourceLang(Lang.RU)
                .targetLang(Lang.EN)
                        .userId("qwe123")
                .build());

        assertThat(instance.checkCustomTranslation(22L, 45L, Lang.RU, Lang.EN, "qwe")).isEmpty();
    }

    @Test
    void testCheckCustomTranslation() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(22L)
                .targetTranslationId(45L)
                .sourceLang(Lang.RU)
                .targetLang(Lang.EN)
                .userId("qwe")
                .build());

        assertThat(instance.checkCustomTranslation(22L, 45L, Lang.RU, Lang.EN, "qwe")).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(wordTranslation));
    }
}
