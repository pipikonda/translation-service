package com.pipikonda.translationbot.telegram.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class TranslatePollInfoDto {

    List<String> answers;
    Integer correctAnswerIndex;
}
