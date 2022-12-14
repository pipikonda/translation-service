package com.pipikonda.translationbot.telegram.service.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallbackDataMapper {

    private static final String DELIMITER = ":";
    private final ObjectMapper objectMapper;

    public String callbackDataToString(CallbackDataDto dto) {
        AtomicReference<String> command = new AtomicReference<>(dto.getCommand().name());
        if (dto.getParams() == null) {
            return command.get();
        }
        dto.getParams().fields()
                .forEachRemaining(e -> command.updateAndGet(v -> v + DELIMITER + e.getValue().asText()));
        return command.get();
    }

    public CallbackDataDto stringToCallbackData(String data) {
        String[] parts = data.split(DELIMITER, 2);
        log.info("parts ==> {}", Arrays.toString(parts));
        CallbackDataCommand command = CallbackDataCommand.valueOf(parts[0]);
        if (parts.length == 1) {
            return CallbackDataDto.builder()
                    .command(command)
                    .build();
        }
        String[] params = parts[1].split(DELIMITER);
        ObjectNode objectNode = objectMapper.createObjectNode();
        List<String> fields = Optional.ofNullable(command.getFields())
                .orElse(List.of());
        if (params.length != fields.size()) {
            throw new BasicLogicException(ErrorCode.VALIDATION_ERROR, "Input callback data params count not equal to command params count");
        }
        for (int i = 0; i < fields.size(); i++) {
            objectNode.put(fields.get(i), params[i]);
        }

        return CallbackDataDto.builder()
                .command(command)
                .params(objectNode)
                .build();
    }
}
