package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.dto.CreateRepeatDto;
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
        //todo if immediatelyRepeat -> call async method

        return repeatRepository.save(Repeat.builder()
                .wordId(dto.getWord().getId())
                .userId(dto.getUserId())
                .sourceLang(dto.getSourceLang())
                .targetLang(dto.getTargetLang())
                .nextRepeat(nextRepeat)
                .build());
    }


}
