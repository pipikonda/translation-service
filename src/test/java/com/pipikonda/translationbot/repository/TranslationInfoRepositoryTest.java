package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.TranslationInfo;
import com.pipikonda.translationbot.domain.TranslationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TranslationInfoRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private TranslationInfoRepository instance;

    @BeforeEach
    @AfterEach
    void cleanDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        TranslationInfo translationInfo = instance.save(TranslationInfo.builder()
                .type(TranslationType.WORD)
                .userId("23L")
                .build());
        assertThat(translationInfo.getId()).isNotNull();
    }

    @Test
    void testFindByIdInAndUserId_shouldReturnEmptyList_whenIsListIsEmpty() {
        instance.save(TranslationInfo.builder()
                .userId("323")
                .type(TranslationType.WORD)
                .build());
        assertThat(instance.findUserTranslations(List.of(), "323")).isEmpty();
    }

    @Test
    void testFindByIdInAndUserId_shouldReturnEmptyList_whenIsIsNotWord() {
        TranslationInfo translationInfo = instance.save(TranslationInfo.builder()
                .userId("323")
                .build());
        assertThat(instance.findUserTranslations(List.of(translationInfo.getId()), "323")).isEmpty();
    }

    @Test
    void testFindByIdInAndUserId_shouldReturnEmptyList_whenUserIdIsNotPresent() {
        TranslationInfo translationInfo = instance.save(TranslationInfo.builder()
                .userId("323")
                .type(TranslationType.WORD)
                .build());
        assertThat(instance.findUserTranslations(List.of(translationInfo.getId()), "232")).isEmpty();
    }

    @Test
    void testFindByIdInAndUserId_shouldReturnEmptyList_whenUserIdIsDefault() {
        TranslationInfo translationInfo = instance.save(TranslationInfo.builder()
                .userId("-1")
                .type(TranslationType.WORD)
                .build());
        assertThat(instance.findUserTranslations(List.of(translationInfo.getId()), "23"))
                .isEqualTo(List.of(translationInfo.getId()));
    }

    @Test
    void testFindByIdInAndUserId_shouldReturnDefaultTranslationId_whenUserIdIsDefault() {
        TranslationInfo translationInfo = instance.save(TranslationInfo.builder()
                .userId("-1")
                .type(TranslationType.WORD)
                .build());
        assertThat(instance.findUserTranslations(List.of(translationInfo.getId()), "23"))
                .isEqualTo(List.of(translationInfo.getId()));
    }

    @Test
    void testFindByIdInAndUserId_shouldReturnCustomTranslationId_whenUserIdIsCustom() {
        TranslationInfo translationInfo = instance.save(TranslationInfo.builder()
                .userId("231")
                .type(TranslationType.WORD)
                .build());
        assertThat(instance.findUserTranslations(List.of(translationInfo.getId()), "231"))
                .isEqualTo(List.of(translationInfo.getId()));
    }

    @Test
    void testFindByIdInAndUserId() {
        TranslationInfo translationInfo1 = instance.save(TranslationInfo.builder()
                .userId("231")
                .type(TranslationType.WORD)
                .build());
        TranslationInfo translationInfo2 = instance.save(TranslationInfo.builder()
                .userId("-1")
                .type(TranslationType.WORD)
                .build());
        TranslationInfo translationInfo3 = instance.save(TranslationInfo.builder()
                .userId("-1")
                .build());
        assertThat(instance.findUserTranslations(List.of(translationInfo1.getId(), translationInfo2.getId()), "231"))
                .containsExactly(translationInfo1.getId(), translationInfo2.getId());
    }
}
