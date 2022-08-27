package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TranslationRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private TranslationRepository instance;

    @BeforeEach
    @AfterEach
    void cleanDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        Translation translation = instance.save(Translation.builder()
                .translationId(22L)
                .value("some translate")
                .lang(Lang.EN)
                .build());

        assertThat(translation.getId()).isNotNull();
    }

    @Test
    void testFindByValueAndLang_shouldReturnEmptyList_whenValueIsNotPresent() {
        assertThat(instance.findByValueAndLang("val", Lang.RU)).isEmpty();
    }

    @Test
    void testFindByValueAndLang_shouldReturnEmptyList_whenLangIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .translationId(22L)
                .value("some translate")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findByValueAndLang("some translate", Lang.RU)).isEmpty();
    }

    @Test
    void testFindByValueAndLang() {
        Translation translation = instance.save(Translation.builder()
                .translationId(22L)
                .value("some translate")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findByValueAndLang("some translate", Lang.EN)).containsOnly(translation);
    }

    @Test
    void testFindByLangAndTranslationIdIn_shouldReturnEmptyList_whenTableIsEmpty() {
        assertThat(instance.findByLangAndTranslationIdIn(Lang.EN, List.of(1L, 2L))).isEmpty();
    }

    @Test
    void testFindByLangAndTranslationIdIn_shouldReturnEmptyList_whenInputIdsIsEmpty() {
        Translation translation = instance.save(Translation.builder()
                .translationId(22L)
                .value("some translate")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findByLangAndTranslationIdIn(Lang.EN, List.of())).isEmpty();
    }

    @Test
    void testFindByLangAndTranslationIdIn_shouldReturnEmptyList_whenLangIsDifferent() {
        Translation translation = instance.save(Translation.builder()
                .translationId(22L)
                .value("some translate")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findByLangAndTranslationIdIn(Lang.RU, List.of(translation.getTranslationId()))).isEmpty();
    }

    @Test
    void testFindByLangAndTranslationIdIn() {
        Translation translation1 = instance.save(Translation.builder()
                .translationId(22L)
                .value("some translate")
                .lang(Lang.EN)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .translationId(24L)
                .value("some translat2e")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findByLangAndTranslationIdIn(Lang.EN, List.of(translation1.getTranslationId())))
                .containsOnly(translation1);
    }
}
