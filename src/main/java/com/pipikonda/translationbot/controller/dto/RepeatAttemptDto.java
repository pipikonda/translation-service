package com.pipikonda.translationbot.controller.dto;

import com.pipikonda.translationbot.telegram.dto.BotAnswerDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RepeatAttemptDto {

    Long attemptId;
    List<BotAnswerDto> values;
    String askedValue;
}
