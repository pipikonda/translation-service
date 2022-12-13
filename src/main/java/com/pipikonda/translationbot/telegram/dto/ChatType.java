package com.pipikonda.translationbot.telegram.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ChatType {

    GROUP,
    PRIVATE;

    private static final Map<String, ChatType> valuesMap = Arrays.stream(values())
            .collect(Collectors.toMap(ChatType::getTextValue, Function.identity()));

    private String getTextValue() {
        return this.name().toLowerCase();
    }

    public static ChatType getByValue(String value) {
        return valuesMap.get(value);
    }
}
