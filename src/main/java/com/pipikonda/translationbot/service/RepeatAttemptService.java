package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Answer;
import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.domain.RepeatAttempt;
import com.pipikonda.translationbot.dto.RepeatAttemptDto;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.AnswerRepository;
import com.pipikonda.translationbot.repository.RepeatAttemptRepository;
import com.pipikonda.translationbot.repository.RepeatRepository;
import com.pipikonda.translationbot.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RepeatAttemptService {

    public static final Integer baseRepeatInterval = 1;
    public static final ChronoUnit repeatIntervalUnit = ChronoUnit.HOURS;
    private static final Integer fakeAnswersCount = 3;
    private final RepeatRepository repeatRepository;
    private final TranslationRepository translationRepository;
    private final RepeatAttemptRepository repeatAttemptRepository;
    private final AnswerRepository answerRepository;
    private final SecureRandom secureRandom;
/*

    @Transactional
    public RepeatAttemptDto createRepeatAttempt(Repeat repeat) {
        repeatRepository.save(repeat.toBuilder()
                .nextRepeat(getNextRepeatTime(repeat.getId()))
                .build());
        Integer attemptNumber = repeatAttemptRepository.findMaxAttemptNumberByRepeatId(repeat.getId());
        RepeatAttempt repeatAttempt = repeatAttemptRepository.save(RepeatAttempt.builder()
                .repeatId(repeat.getId())
                .attemptNumber(attemptNumber == null ? 1 : ++attemptNumber)
                .build());
        String correctAnswer = translationRepository.findUserTranslateByWordIdAndLang(Set.of(repeat.getWordId()), repeat.getTargetLang(), repeat.getUserId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND,
                        "Not found correct translation for wordId " + repeat.getWordId() + " lang " + repeat.getTargetLang()));
        List<String> translations = translationRepository.findDistinctValuesByLang(repeat.getTargetLang(), correctAnswer);

        List<String> randomValues = secureRandom.ints(fakeAnswersCount, 0, translations.size())
                .mapToObj(translations::get)
                .toList();
        saveAnswers(randomValues, correctAnswer, repeatAttempt.getId());

        return RepeatAttemptDto.builder()
                .attemptId(repeatAttempt.getId())
                .values(mixAnswers(randomValues, correctAnswer))
                .build();
    }

    private List<String> mixAnswers(List<String> answers, String correctAnswer) {
        answers.add(correctAnswer);
        Collections.shuffle(answers);
        return answers;
    }

    private void saveAnswers(List<String> fakeAnswers, String correctAnswer, Long repeatAttemptId) {
        fakeAnswers.stream()
                .map(e -> Answer.builder()
                        .textValue(e)
                        .isCorrect(false)
                        .repeatAttemptId(repeatAttemptId)
                        .build())
                .forEach(answerRepository::save);
        answerRepository.save(Answer.builder()
                .textValue(correctAnswer)
                .isCorrect(true)
                .repeatAttemptId(repeatAttemptId)
                .build());
    }

    /**
     * repeat delay = baseRepeatInterval * 2 ^ attemptNumber
     */
    private Instant getNextRepeatTime(Long repeatId) {
        long successAttemptCount = repeatAttemptRepository.countByRepeatIdAndIsSuccessIsTrue(repeatId);
        long delay = (long) (baseRepeatInterval * Math.pow(2, successAttemptCount));
        return Instant.now().plus(delay, repeatIntervalUnit);
    }
}
