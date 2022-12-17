package com.pipikonda.translationbot.error;

import com.pipikonda.translationbot.telegram.TranslateBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    private final TranslateBot translateBot;
    private final MessageSource messageSource;
    private final String chatId;

    public CustomExceptionHandler(TranslateBot translateBot,
                                  MessageSource messageSource,
                                  @Value("${service.telegram.exceptions.chat-id}") String chatId) {
        this.translateBot = translateBot;
        this.messageSource = messageSource;
        this.chatId = chatId;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BasicLogicException.class)
    public ErrorResponse handle(BasicLogicException exception) {
        logException(exception);
        return ErrorResponse.builder()
                .errorCode(exception.getErrorCode())
                .errorText(exception.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handle(MethodArgumentNotValidException exception) {
        logException(exception);
        String errorMessage = exception.getBindingResult().getAllErrors()
                .stream()
                .map(e -> e.getObjectName() + " " + e.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return ErrorResponse.builder()
                .errorCode(ErrorCode.VALIDATION_ERROR)
                .errorText(errorMessage)
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handle(Exception exception) throws TelegramApiException {
        logException(exception);
        Object[] params = new Object[]{exception.getCause().getClass(), exception.getMessage()};
        translateBot.execute(SendMessage.builder()
                .chatId(chatId)
                .text(messageSource.getMessage("telegram.exception.message-text", params, Locale.getDefault()))
                .build());
        return ErrorResponse.builder()
                .errorCode(ErrorCode.UNKNOWN_ERROR)
                .errorText(exception.getMessage())
                .build();
    }

    private void logException(Throwable throwable) {
        log.error("Exception handler got exception: ", throwable);
    }
}
