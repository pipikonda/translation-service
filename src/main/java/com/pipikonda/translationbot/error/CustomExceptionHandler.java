package com.pipikonda.translationbot.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BasicLogicException.class)
    public ErrorResponse handle(BasicLogicException exception) {
        log.error("Got exception", exception);
        return ErrorResponse.builder()
                .errorCode(exception.getErrorCode())
                .errorText(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handle(MethodArgumentNotValidException exception) {
//        log.error("Got validation exception", exception);
        String errorMessage = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return ErrorResponse.builder()
                .errorCode(ErrorCode.VALIDATION_ERROR)
                .errorText(errorMessage)
                .build();
    }
}
