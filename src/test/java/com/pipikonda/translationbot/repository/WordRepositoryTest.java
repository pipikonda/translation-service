package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.domain.Word;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WordRepositoryTest extends TestContainerBaseClass {

    @Autowired
    private WordRepository instance;

    @BeforeEach
    @AfterEach
    void cleanDb() {
        instance.deleteAll();
    }

    @Test
    void testSave() {
        Word word = instance.save(Word.builder()
                .build());
        assertThat(word.getId()).isNotNull();
    }
}
