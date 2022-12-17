package com.pipikonda.translationbot.telegram.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Locale;

@Builder
@Value
public class GetSettingsInfoDto {

    Long chatId;
    Locale userLocale;
    Object[] params;
}
