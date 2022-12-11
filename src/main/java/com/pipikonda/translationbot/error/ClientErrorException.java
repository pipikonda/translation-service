package com.pipikonda.translationbot.error;

import lombok.Getter;

@Getter
public class ClientErrorException extends RuntimeException {

    private ErrorCode errorCode;

    public ClientErrorException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
