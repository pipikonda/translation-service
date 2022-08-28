package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
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
                .textValue("some translate")
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
                .textValue("another text")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "-1")).isEmpty();
    }

    @Test
    void testFindUserWordsId_shouldReturnEmptySet_whenLangIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "-1")).isEmpty();
    }

    @Test
    void testFindUserWordsId_shouldReturnEmptySet_whenUserIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("23")
                .textValue("text")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "789")).isEmpty();
    }

    @Test
    void testFindUserWordsId_shouldReturnWordId_whenUserIsDefault() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "789")).containsOnly(translation.getWordId());
    }

    @Test
    void testFindUserWordsId_shouldReturnOneWordId_whenWordIdAreDuplicates() {
        Translation translation1 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text")
                .lang(Lang.EN)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserWordsId("text", Lang.EN, "789")).containsOnly(translation1.getWordId());
    }

    @Test
    void testFindUserWordsId() {
        Translation translation1 = instance.save(Translation.builder()
                .wordId(5L)
                .userId("-1")
                .textValue("text")
                .lang(Lang.EN)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("789")
                .textValue("text")
                .lang(Lang.EN)
                .build());
        Translation translation3 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text 3")
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
                .textValue("text 3")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 4L), Lang.RU, "-1")).isEmpty();
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnEmptyList_whenLangIsIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text 3")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "-1")).isEmpty();
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnEmptyList_whenUserIdIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("789")
                .textValue("text 3")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "456")).isEmpty();
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnTranslate_whenUserIdIsDefault() {
        Translation translation = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text 3")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "456"))
                .containsOnly(translation.getTextValue());
    }

    @Test
    void testFindUserTranslateByWordIdAndLang_shouldReturnTranslations_whenWordsIdAreDuplicates() {
        Translation translation1 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text 3")
                .lang(Lang.RU)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text 2")
                .lang(Lang.RU)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L), Lang.RU, "456"))
                .containsOnly(translation1.getTextValue(), translation2.getTextValue());
    }

    @Test
    void testFindUserTranslateByWordIdAndLang() {
        Translation translation1 = instance.save(Translation.builder()
                .wordId(2L)
                .userId("-1")
                .textValue("text 3")
                .lang(Lang.RU)
                .build());
        Translation translation2 = instance.save(Translation.builder()
                .wordId(4L)
                .userId("456")
                .textValue("text 2")
                .lang(Lang.RU)
                .build());
        Translation translation3 = instance.save(Translation.builder()
                .wordId(1L)
                .userId("-1")
                .textValue("text 2")
                .lang(Lang.EN)
                .build());
        assertThat(instance.findUserTranslateByWordIdAndLang(Set.of(1L, 2L, 4L), Lang.RU, "456"))
                .containsOnly(translation1.getTextValue(), translation2.getTextValue());
    }

    @Test
    void testFindDistinctValuesByLang_shouldReturnEmptyList_whenTableIsEmpty() {
        assertThat(instance.findDistinctValuesByLang(Lang.EN, "")).isEmpty();
    }

    @Test
    void testFindDistinctValuesByLang_shouldReturnEmptyList_whenLangIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .lang(Lang.RU)
                .userId("-1")
                .build());
        assertThat(instance.findDistinctValuesByLang(Lang.EN, "")).isEmpty();
    }

    @Test
    void testFindDistinctValuesByLang_shouldReturnEmptyList_whenTextValueIsNull() {
        Translation translation = instance.save(Translation.builder()
                .lang(Lang.RU)
                .userId("-1")
                .build());
        assertThat(instance.findDistinctValuesByLang(Lang.RU, "")).isEmpty();
    }

    @Test
    void testFindDistinctValuesByLang_shouldReturnOnlyOneValue_whenValuesAreEquals() {
        for (int i = 0; i < 5; i++) {
            Translation translation = instance.save(Translation.builder()
                    .lang(Lang.RU)
                    .textValue("value")
                    .userId("-1")
                    .build());
        }
        assertThat(instance.findDistinctValuesByLang(Lang.RU, "")).containsOnly("value");
    }

    @Test
    void testFindDistinctValuesByLang_shouldReturnEmptyList_whenUserIdIsNotDefault() {
        Translation translation = instance.save(Translation.builder()
                .lang(Lang.RU)
                .textValue("value")
                .userId("123")
                .build());
        assertThat(instance.findDistinctValuesByLang(Lang.RU, "")).isEmpty();
    }

    @Test
    void testFindDistinctValuesByLang_shouldReturnEmptyList_whenValueIsEqualToExcludedValue() {
        Translation translation = instance.save(Translation.builder()
                .lang(Lang.RU)
                .textValue("value")
                .userId("-1")
                .build());
        assertThat(instance.findDistinctValuesByLang(Lang.RU, "value")).isEmpty();
    }

    @Test
    void testFindDistinctValuesByLang() {
        for (int i = 0; i < 2000; i++) {
            Translation translation = instance.save(Translation.builder()
                    .lang(Lang.RU)
                    .textValue("value" + i)
                    .userId("-1")
                    .build());
        }
        assertThat(instance.findDistinctValuesByLang(Lang.RU, "").size()).isEqualTo(1000);
    }
}
