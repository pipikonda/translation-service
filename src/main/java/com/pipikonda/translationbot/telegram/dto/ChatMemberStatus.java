package com.pipikonda.translationbot.telegram.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ChatMemberStatus {

    CREATOR,
    ADMINISTRATOR,
    MEMBER,
    RESTRICTED,
    LEFT,
    KICKED;

    private static final Map<String, ChatMemberStatus> valuesMap = Arrays.stream(values())
            .collect(Collectors.toMap(ChatMemberStatus::getTextValue, Function.identity()));

    private String getTextValue() {
        return this.name().toLowerCase();
    }

    public static ChatMemberStatus getByValue(String value) {
        return valuesMap.get(value);
    }
}
