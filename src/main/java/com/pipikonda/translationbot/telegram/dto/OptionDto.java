package com.pipikonda.translationbot.telegram.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class OptionDto {

    String value;
    Long answerId;
}
