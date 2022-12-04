package com.pipikonda.translationbot.telegram.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Locale;

@Builder
@Value
public class TranslationBotResponse {

    Long chatId;
    List<String> translations;
    Locale userLocale;
    String word;
}
