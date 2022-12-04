package com.pipikonda.translationbot.controller.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Value
public class CreateRepeatDto {

    @NotNull
    Long wordTranslationId;

    @NotBlank
    String userId;
    Boolean immediatelyRepeat;
}
