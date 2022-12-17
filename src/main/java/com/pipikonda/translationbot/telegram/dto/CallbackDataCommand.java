package com.pipikonda.translationbot.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CallbackDataCommand {

    TRANSLATE_WORD,
    ANSWER(List.of("attemptId", "answerId")),
    GET_RANDOM_WORD,
    GET_BOT_INFO,
    USER_SETTINGS,
    CHANGE_LANGS,
    SET_SOURCE_LANG(List.of("source")),
    SET_TARGET_LANG(List.of("source", "target")),
    BACK_TO_MENU;

    List<String> fields;
}
