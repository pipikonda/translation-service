package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.controller.dto.CreateRepeatDto;
import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.RepeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RepeatService {

    private final RepeatRepository repeatRepository;

    public Repeat createNewRepeat(CreateRepeatDto dto) {
        Instant nextRepeat =
                dto.getImmediatelyRepeat() ?
                        Instant.now() :
                        Instant.now().plus(RepeatAttemptService.baseRepeatInterval, RepeatAttemptService.repeatIntervalUnit);
        //todo if immediatelyRepeat -> call poll async

        repeatRepository.findByUserIdAndWordTranslationId(dto.getUserId(), dto.getWordTranslationId())
                .ifPresent(e -> {
                    throw new BasicLogicException(ErrorCode.BAD_REQUEST,
                            "Repeat with userId " + dto.getUserId() + "and " + dto.getWordTranslationId() + " is already exists");
                });

        return repeatRepository.save(Repeat.builder()
                .userId(dto.getUserId())
                .nextRepeat(nextRepeat)
                .wordTranslationId(dto.getWordTranslationId())
                .build());
    }
}
