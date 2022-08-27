package com.pipikonda.translationbot.dto;

import com.pipikonda.translationbot.domain.Lang;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class WordResponseDto {

    Long id;
    Lang sourceLang;
    Lang targetLang;
    String inputValue;
    List<String> translations;
}
