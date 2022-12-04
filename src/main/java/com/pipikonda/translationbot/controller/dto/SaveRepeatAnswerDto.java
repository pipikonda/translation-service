package com.pipikonda.translationbot.controller.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Value
public class SaveRepeatAnswerDto {

    @NotNull
    Long repeatAttemptId;

    @NotBlank
    String answer;
}
