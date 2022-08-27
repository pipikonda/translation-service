package com.pipikonda.translationbot.error;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ErrorResponse {

    ErrorCode errorCode;
    String errorText;
}
