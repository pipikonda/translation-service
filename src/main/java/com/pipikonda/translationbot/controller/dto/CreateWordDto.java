package com.pipikonda.translationbot.controller.dto;

import com.pipikonda.translationbot.controller.validation.CheckTranslateLang;
import com.pipikonda.translationbot.domain.Lang;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Value
@Jacksonized
@CheckTranslateLang
public class CreateWordDto {

    @NotBlank
    String word;

    @NotNull
    Lang sourceLang;

    @NotNull
    Lang targetLang;

    List<String> translations;
}
