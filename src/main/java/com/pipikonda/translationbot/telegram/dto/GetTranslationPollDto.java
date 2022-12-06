package com.pipikonda.translationbot.telegram.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Locale;

@Builder
@Value
public class GetTranslationPollDto {

    Long chatId;
    String askedValue;
    Locale userLocale;
    List<String> options;
    Long repeatAttempt;
}
