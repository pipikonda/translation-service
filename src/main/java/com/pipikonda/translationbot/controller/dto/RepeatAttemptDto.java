package com.pipikonda.translationbot.controller.dto;

import com.pipikonda.translationbot.telegram.dto.OptionDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RepeatAttemptDto {

    Long attemptId;
    List<OptionDto> values;
    String askedValue;
}
