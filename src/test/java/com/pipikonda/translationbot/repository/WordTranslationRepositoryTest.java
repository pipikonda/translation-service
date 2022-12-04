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

    @Test
    void testGetFakeAnswersId_shouldReturnEmpty_whenLangIsNotPresent() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(22L)
                .targetTranslationId(45L)
                .sourceLang(Lang.RU)
                .targetLang(Lang.UK)
                .build());

        assertThat(instance.getFakeAnswersId(Lang.EN, 2L, "qwe")).isEmpty();
    }

    @Test
    void testGetFakeAnswersId_shouldReturnEmpty_whenTargetIdIsEqualToCorrectAnswer() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(22L)
                .targetTranslationId(2L)
                .sourceLang(Lang.RU)
                .targetLang(Lang.EN)
                .build());

        assertThat(instance.getFakeAnswersId(Lang.EN, 2L, "qwe")).isEmpty();
    }

    @Test
    void testGetFakeAnswersId_shouldReturnResult_whenUserIdIsNull() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(22L)
                .targetTranslationId(27L)
                .sourceLang(Lang.RU)
                .targetLang(Lang.EN)
                .build());

        assertThat(instance.getFakeAnswersId(Lang.EN, 2L, "qwe"))
                .containsOnly(wordTranslation.getTargetTranslationId());
    }

    @Test
    void testGetFakeAnswersId_shouldReturnResult_whenUserIdIsEqual() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(22L)
                .targetTranslationId(27L)
                .sourceLang(Lang.RU)
                .targetLang(Lang.EN)
                .userId("qwe")
                .build());

        assertThat(instance.getFakeAnswersId(Lang.EN, 2L, "qwe"))
                .containsOnly(wordTranslation.getTargetTranslationId());
    }

    @Test
    void findByIdAndUserId_shouldReturnEmpty_whenWordTranslationIsNotPresent() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(23L)
                .targetTranslationId(12L)
                .sourceLang(Lang.EN)
                .targetLang(Lang.RU)
                .build());

        assertThat(instance.findByIdAndUserId(-1L, "qwe")).isEmpty();
    }

    @Test
    void findByIdAndUserId_shouldReturnEmpty_whenUserIdIsNotEqual() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(23L)
                .targetTranslationId(12L)
                .sourceLang(Lang.EN)
                .targetLang(Lang.RU)
                .userId("asd")
                .build());

        assertThat(instance.findByIdAndUserId(wordTranslation.getId(), "qwe")).isEmpty();
    }

    @Test
    void findByIdAndUserId_shouldReturnResult_whenUserIdIsNull() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(23L)
                .targetTranslationId(12L)
                .sourceLang(Lang.EN)
                .targetLang(Lang.RU)
                .build());

        assertThat(instance.findByIdAndUserId(wordTranslation.getId(), "qwe")).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(wordTranslation));
    }

    @Test
    void findByIdAndUserId_shouldReturnResult_whenUserIdIsEqual() {
        WordTranslation wordTranslation = instance.save(WordTranslation.builder()
                .sourceTranslationId(23L)
                .targetTranslationId(12L)
                .sourceLang(Lang.EN)
                .targetLang(Lang.RU)
                .userId("qwe")
                .build());

        assertThat(instance.findByIdAndUserId(wordTranslation.getId(), "qwe")).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(wordTranslation));
    }
}
