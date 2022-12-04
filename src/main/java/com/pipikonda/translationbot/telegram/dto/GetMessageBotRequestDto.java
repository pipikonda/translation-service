package com.pipikonda.translationbot.telegram.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Locale;

@Builder
@Value
public class GetMessageBotRequestDto {

    Long chatId;
    Locale userLocale;
    String messagePattern;
    Object[] params;

}
