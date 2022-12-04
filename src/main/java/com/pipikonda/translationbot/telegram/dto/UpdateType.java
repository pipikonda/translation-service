package com.pipikonda.translationbot.telegram.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum UpdateType {

    MESSAGE("message"),
    MY_CHAT_MEMBER("my_chat_member"),
    CALLBACK_QUERY("callback_query");

    private static final Map<String, UpdateType> map = Arrays.stream(values())
            .collect(Collectors.toMap(UpdateType::getFiledName, Function.identity()));

    private final String filedName;

    public static UpdateType getByFieldName(String fieldName) {
        return map.get(fieldName);
    }
}
