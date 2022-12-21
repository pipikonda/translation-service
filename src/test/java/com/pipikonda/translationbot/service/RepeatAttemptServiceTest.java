package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.TestContainerBaseClass;
import com.pipikonda.translationbot.controller.dto.RepeatAttemptDto;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.domain.RepeatAttempt;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.domain.WordTranslation;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.repository.AnswerRepository;
import com.pipikonda.translationbot.repository.RepeatAttemptRepository;
import com.pipikonda.translationbot.repository.RepeatRepository;
import com.pipikonda.translationbot.repository.TranslationRepository;
import com.pipikonda.translationbot.repository.WordTranslationRepository;
import com.pipikonda.translationbot.telegram.dto.OptionDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class RepeatAttemptServiceTest extends TestContainerBaseClass {

    @Autowired
    private RepeatAttemptService instance;

    @Autowired
    private RepeatRepository repeatRepository;

    @Autowired
    private WordTranslationRepository wordTranslationRepository;

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private RepeatAttemptRepository repeatAttemptRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @BeforeEach
    @AfterEach
    void clearDb() {
        repeatRepository.deleteAll();
        translationRepository.deleteAll();
        wordTranslationRepository.deleteAll();
        repeatAttemptRepository.deleteAll();
        answerRepository.deleteAll();
    }

    @Test
    void createRepeatAttempt_shouldThrowException_whenRepeatWordTranslationIsNotExists() {
        Repeat repeat = repeatRepository.save(Repeat.builder()
                .wordTranslationId(555L)
                .userId("some user id")
                .build());
        assertThatExceptionOfType(BasicLogicException.class)
                .isThrownBy(() -> instance.createRepeatAttempt(repeat))
                .withMessage("Not found wordTranslation when expected");
    }

    @Test
    void createRepeatAttempt_shouldThrowException_whenTargetTranslationIsNotExists() {
        WordTranslation wordTranslation = wordTranslationRepository.save(WordTranslation.builder()
                .userId("some user id")
                .targetLang(Lang.RU)
                .sourceLang(Lang.EN)
                .targetTranslationId(23L)
                .sourceTranslationId(45L)
                .build());
        Repeat repeat = repeatRepository.save(Repeat.builder()
                .wordTranslationId(wordTranslation.getId())
                .userId("some user id")
                .build());
        assertThatExceptionOfType(BasicLogicException.class)
                .isThrownBy(() -> instance.createRepeatAttempt(repeat))
                .withMessage("Not found correct translation for source translation id " + 23);
    }

    @Test
    void testCreateRepeatAttempt() {
        Translation translation1 = translationRepository.save(Translation.builder()
                .textValue("cat")
                .build());
        Translation translation2 = translationRepository.save(Translation.builder()
                .textValue("кот")
                .build());
        Translation translation3 = translationRepository.save(Translation.builder()
                .textValue("собака")
                .build());
        Translation translation4 = translationRepository.save(Translation.builder()
                .textValue("dog")
                .build());

        WordTranslation wordTranslation1 = wordTranslationRepository.save(WordTranslation.builder()
                .userId("some user id")
                .targetLang(Lang.RU)
                .sourceLang(Lang.EN)
                .sourceTranslationId(translation2.getId())
                .targetTranslationId(translation1.getId())
                .build());
        WordTranslation wordTranslation2 = wordTranslationRepository.save(WordTranslation.builder()
                .userId("some user id")
                .targetLang(Lang.RU)
                .sourceLang(Lang.EN)
                .sourceTranslationId(translation3.getId())
                .targetTranslationId(translation4.getId())
                .build());

        Repeat repeat = repeatRepository.save(Repeat.builder()
                .wordTranslationId(wordTranslation2.getId())
                .userId("some user id")
                .build());

        RepeatAttemptDto repeatAttempt = instance.createRepeatAttempt(repeat);

        System.out.println("====> " + repeatAttempt.getValues());
        Optional<RepeatAttempt> attempt = repeatAttemptRepository.findAll().stream().peek(e -> System.out.println("===== >>> " + e)).findFirst();
        assertThat(attempt).isPresent()
                .hasValueSatisfying(e -> assertThat(e.getRepeatId()).isEqualTo(repeat.getId()));
        assertThat(repeatAttempt.getValues()).contains(OptionDto.builder()
                .value("dog")
                .answerId(translation4.getId())
                .build());
        assertThat(repeatAttempt.getValues().size()).isEqualTo(2);
    }

    @Test
    void testNeedRepeatAttempt_shouldReturnTrue_whenAttemptIsNotFound() {
        RepeatAttempt repeatAttempt = repeatAttemptRepository.save(RepeatAttempt.builder()
                .repeatId(12L)
                .build());

        assertThat(instance.needRepeatAttempt(666L)).isTrue();
    }

    @Test
    void testNeedRepeatAttempt_shouldReturnTrue_whenAttemptHasUserAnswer() {
        RepeatAttempt repeatAttempt = repeatAttemptRepository.save(RepeatAttempt.builder()
                .created(Instant.now())
                .userAnswerId(12L)
                .repeatId(12L)
                .build());

        assertThat(instance.needRepeatAttempt(12L)).isTrue();
    }

    @Test
    void testNeedRepeatAttempt_shouldReturnTrue_whenCreatedMoreThanDayAgo() {
        RepeatAttempt repeatAttempt = repeatAttemptRepository.save(RepeatAttempt.builder()
                .created(Instant.now().minus(2, ChronoUnit.DAYS))
                .repeatId(12L)
                .build());

        assertThat(instance.needRepeatAttempt(12L)).isTrue();
    }

    @Test
    void testNeedRepeatAttempt_shouldReturnFalse() {
        RepeatAttempt repeatAttempt = repeatAttemptRepository.save(RepeatAttempt.builder()
                .created(Instant.now().minus(2, ChronoUnit.HOURS))
                .repeatId(12L)
                .build());

        assertThat(instance.needRepeatAttempt(12L)).isFalse();
    }
}