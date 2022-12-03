package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Translation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                    .textValue("some translate")
                    .build());

        assertThat(translation.getId()).isNotNull();
        assertThat(instance.findById(translation.getId())).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(translation));
    }

    @Test
    void testFindByTextValue() {
        Translation translation = instance.save(Translation.builder()
                .textValue("some translate")
                .build());

        assertThat(instance.findByTextValue("some translate")).isPresent()
                .hasValueSatisfying(e -> assertThat(e).isEqualTo(translation));
    }

    @Test
    void testFindByTextValue_shouldReturnEmpty_whenTranslateIsNotPresent() {
        Translation translation = instance.save(Translation.builder()
                .textValue("some translate")
                .build());

        assertThat(instance.findByTextValue("another translate")).isEmpty();
    }
}
