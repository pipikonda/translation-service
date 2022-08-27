package com.pipikonda.translationbot.dto;

import com.pipikonda.translationbot.domain.Lang;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
public class WordTranslateDto {

    @NotNull
    Lang sourceLang;

    @NotNull
    Lang targetLang;

    @NotEmpty
    String value;

    @NotNull
    String userId;
}
