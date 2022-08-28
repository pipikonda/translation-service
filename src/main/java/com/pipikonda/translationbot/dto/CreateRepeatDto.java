package com.pipikonda.translationbot.dto;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Word;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CreateRepeatDto {

    Word word;
    Lang targetLang;
    Lang sourceLang;
    String userId;
    Boolean immediatelyRepeat;
}
