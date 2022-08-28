package com.pipikonda.translationbot.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
@Builder
public class RepeatAttemptDto {

    Long attemptId;
    List<String> values;
}
