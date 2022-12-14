package com.pipikonda.translationbot.error;

import lombok.Getter;

@Getter
public class BasicLogicException extends RuntimeException {

    private ErrorCode errorCode;

    public BasicLogicException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
