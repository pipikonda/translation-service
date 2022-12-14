package com.pipikonda.translationbot.controller.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Builder
@Value
@Jacksonized
public class CreateRepeatAttemptDto {

    @NotNull
    Long repeatId;
}
