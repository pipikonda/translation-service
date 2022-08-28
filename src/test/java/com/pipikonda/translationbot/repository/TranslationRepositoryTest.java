package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

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
                .wordId(22L)
                .value("some translate")
                .lang(Lang.EN)
                .build());

        assertThat(translation.getId()).isNotNull();
    }

    @Test
    void testFindUserWordsId_shouldReturnEmptySet_whenTableIsEmpty() {
        assertThat(instance.findUserWordsId("text", Lang.EN, "-1")).isEmpty();
    }

    @Test
    void testFindUserWordsId_shouldReturnEmptySet_whenValueIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                        .wordId(2L)
                        .userId("-1")
                        .value("another text")
                        .lang(Lang.EN)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "-1")).isEmpty();
    }

    @Test
    void testFindUserWordsId_shouldReturnEmptySet_whenLangIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "-1")).isEmpty();
    }

    @Test
    void testFindUserWordsId_shouldReturnEmptySet_whenUserIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("23")
                .value("text")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "789")).isEmpty();
    }

    @Test
    void testFindUserWordsId_shouldReturnWordId_whenUserIsDefault() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "789")).containsOnly(translation.getWordId());
    }

    @Test
    void testFindUserWordsId_shouldReturnOneWordId_whenWordIdAreDuplicates() {
        Translation translation1 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text")
                .lang(Lang.EN)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "789")).containsOnly(translation1.getWordId());
    }

    @Test
    void testFindUserWordsId() {
        Translation translation1 = instance.save(Translation.builder()
                .wordId(5L)
                .userId("-1")
                .value("text")
                .lang(Lang.EN)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("789")
                .value("text")
                .lang(Lang.EN)
                .build());
        Translation translation3 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text 3")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "789"))
                .containsOnly(translation1.getWordId(), translation2.getWordId());
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnEmptyList_whenTableIsEmpty() {
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "-1")).isEmpty();
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnEmptyList_whenWordIdIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text 3")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 4L), Lang.RU, "-1")).isEmpty();
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnEmptyList_whenLangIsIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text 3")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "-1")).isEmpty();
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnEmptyList_whenUserIdIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("789")
                .value("text 3")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "456")).isEmpty();
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnTranslate_whenUserIdIsDefault() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text 3")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "456"))
                .containsOnly(translation.getValue());
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnTranslations_whenWordsIdAreDuplicates() {
        Translation translation1 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text 3")
                .lang(Lang.RU)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text 2")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "456"))
                .containsOnly(translation1.getValue(), translation2.getValue());
    }

    @Test
    void testFindUserTranslateByWordIdAndLang() {
        Translation translation1 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .value("text 3")
                .lang(Lang.RU)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .wordId(4L)
                .userId("456")
                .value("text 2")
                .lang(Lang.RU)
                .build());
        Translation translation3 = instance.save(Translation.builder()
                .wordId(1L)
                .userId("-1")
                .value("text 2")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L, 4L), Lang.RU, "456"))
                .containsOnly(translation1.getValue(), translation2.getValue());
    }
}
