package com.pipikonda.translationbot.telegram.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CallbackDataDto {

    CallbackDataCommand value;
}
