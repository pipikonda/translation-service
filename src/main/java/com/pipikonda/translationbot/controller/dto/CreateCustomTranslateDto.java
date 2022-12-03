package com.pipikonda.translationbot.controller.dto;

import com.pipikonda.translationbot.domain.Lang;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Value
public class CreateCustomTranslateDto {

    @NotBlank
    String sourceValue;

    @NotBlank
    String targetValue;

    @NotNull
    Lang sourceLang;

    @NotNull
    Lang targetLang;

    @NotBlank
    String userId;
}
