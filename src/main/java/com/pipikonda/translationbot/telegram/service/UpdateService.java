package com.pipikonda.translationbot.telegram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.telegram.dto.UpdateType;
import com.pipikonda.translationbot.telegram.service.handlers.updates.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UpdateService {

    private final ObjectMapper objectMapper;
    private final Map<UpdateType, UpdateHandler> updateHandlers;

    public UpdateService(ObjectMapper objectMapper, List<UpdateHandler> updateHandlers) {
        this.objectMapper = objectMapper;
        this.updateHandlers = updateHandlers.stream()
                .collect(Collectors.toMap(UpdateHandler::getUpdateType, Function.identity()));
    }

    public void handleUpdate(Update update) {
        UpdateType type = getUpdateType(update);
        UpdateHandler updateHandler = updateHandlers.get(type);
        Optional.ofNullable(updateHandler)
                .ifPresentOrElse(e -> {
                            try {
                                e.handleUpdate(update);
                            } catch (TelegramApiException | JsonProcessingException ex) {
                                log.error("Update handler got exception", ex);
                                throw new RuntimeException(ex);
                            }
                        },
                        () -> log.warn("Not found handler for update type {}", type));
    }

    private UpdateType getUpdateType(Update update) {
        Map<String, Object> fields = objectMapper.convertValue(update, Map.class);
        String fieldName = fields.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals("update_id"))
                .filter(e -> e.getValue() != null)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new BasicLogicException(ErrorCode.BAD_REQUEST, "Input update is empty"));

        return Optional.ofNullable(UpdateType.getByFieldName(fieldName))
                .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found update type by field name " + fieldName));
    }
}
