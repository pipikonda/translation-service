package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Answer;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.domain.RepeatAttempt;
import com.pipikonda.translationbot.domain.RepeatType;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.domain.WordTranslation;
import com.pipikonda.translationbot.controller.dto.RepeatAttemptDto;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.AnswerRepository;
import com.pipikonda.translationbot.repository.RepeatAttemptRepository;
import com.pipikonda.translationbot.repository.RepeatRepository;
import com.pipikonda.translationbot.repository.TranslationRepository;
import com.pipikonda.translationbot.repository.WordTranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final WordTranslationRepository wordTranslationRepository;
    private final TranslationService translationService;

    @Transactional
    public boolean saveAnswer(Long repeatAttemptId, String answer) {
        RepeatAttempt repeatAttempt = repeatAttemptRepository.findById(repeatAttemptId)
                .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found repeat attempt with id " + repeatAttemptId));
        if (repeatAttempt.getUserAnswerId() != null) {
            throw new BasicLogicException(ErrorCode.BAD_REQUEST, "Attempt already has answer");
        }
        Long answerId = translationService.getTranslationByValue(answer).getId();
        boolean isAnswerCorrect =
                answerRepository.findByRepeatAttemptIdAndTranslationValueIdAndIsCorrectIsTrue(repeatAttemptId, answerId)
                        .isPresent();
        repeatAttemptRepository.save(repeatAttempt.toBuilder()
                        .userAnswerId(answerId)
                        .attemptTime(Instant.now())
                        .isSuccess(isAnswerCorrect)
                .build());
        return isAnswerCorrect;
    }

    @Transactional
    public RepeatAttemptDto createRepeatAttempt(Long repeatId) {
        Repeat repeat = repeatRepository.findById(repeatId)
                .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found repeat by id " + repeatId));
        repeatRepository.save(repeat.toBuilder()
                .nextRepeat(getNextRepeatTime(repeat.getId()))
                .build());
        Integer attemptNumber = repeatAttemptRepository.findMaxAttemptNumberByRepeatId(repeat.getId());
        RepeatAttempt repeatAttempt = repeatAttemptRepository.save(RepeatAttempt.builder()
                .repeatId(repeat.getId())
                .repeatType(RepeatType.TRANSLATE_TEST)
                .attemptNumber(
                        Optional.ofNullable(attemptNumber)
                                .map(e -> ++e)
                                .orElse(1)
                ).build());

        WordTranslation wordTranslation = wordTranslationRepository.findByIdAndUserId(repeat.getWordTranslationId(), repeat.getUserId())
                .orElseThrow(() -> new BasicLogicException(ErrorCode.UNKNOWN_ERROR, "Not found wordTranslation when expected"));
        Translation correctAnswer = translationRepository.findById(wordTranslation.getTargetTranslationId())
                .orElseThrow(() -> new BasicLogicException(ErrorCode.UNKNOWN_ERROR,
                        "Not found correct translation for source translation id " + wordTranslation.getTargetTranslationId()));
        List<Translation> fakeAnswers = getFakeAnswers(wordTranslation.getTargetLang(), wordTranslation.getTargetTranslationId(), repeat.getUserId());
        saveAnswers(fakeAnswers, correctAnswer, repeatAttempt.getId());

        return RepeatAttemptDto.builder()
                .attemptId(repeatAttempt.getId())
                .values(mixAnswers(fakeAnswers, correctAnswer))
                .build();
    }

    private List<Translation> getFakeAnswers(Lang targetLang, Long correctTranslationId, String userId) {
        List<Long> fakeAnswersId = wordTranslationRepository.getFakeAnswersId(targetLang, correctTranslationId, userId);
        List<Translation> fakeAnswers = translationRepository.findAllById(fakeAnswersId);
        return secureRandom.ints(fakeAnswersCount, 0, fakeAnswers.size())
                .mapToObj(fakeAnswers::get)
                .collect(Collectors.toList());
    }

    private List<String> mixAnswers(List<Translation> answers, Translation correctAnswer) {
        answers.add(correctAnswer);
        Collections.shuffle(answers);
        return answers.stream()
                .map(Translation::getTextValue)
                .toList();
    }

    private void saveAnswers(List<Translation> fakeAnswers, Translation correctAnswer, Long repeatAttemptId) {
        fakeAnswers.stream()
                .map(e -> Answer.builder()
                        .translationValueId(e.getId())
                        .isCorrect(false)
                        .repeatAttemptId(repeatAttemptId)
                        .build())
                .forEach(answerRepository::save);
        answerRepository.save(Answer.builder()
                .translationValueId(correctAnswer.getId())
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
