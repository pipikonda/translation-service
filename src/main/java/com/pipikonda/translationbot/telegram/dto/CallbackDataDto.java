package com.pipikonda.translationbot.telegram.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CallbackDataDto {

    CallbackDataCommand command;
    JsonNode params;
}
