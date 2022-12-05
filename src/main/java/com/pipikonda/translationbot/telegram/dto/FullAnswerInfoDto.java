package com.pipikonda.translationbot.telegram.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class FullAnswerInfoDto {

    String value;
    Long translationId;
    boolean correct;
}
