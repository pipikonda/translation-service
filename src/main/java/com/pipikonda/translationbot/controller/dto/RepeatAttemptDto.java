package com.pipikonda.translationbot.controller.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RepeatAttemptDto {

    Long attemptId;
    List<String> values;
    String askedValue;
}
