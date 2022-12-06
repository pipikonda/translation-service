package com.pipikonda.translationbot.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CallbackDataCommand {

    TRANSLATE_WORD(List.of("attemptId", "translationId")),
    ANSWER,
    GET_RANDOM_WORD,
    GET_BOT_INFO,
    BACK_TO_MENU;

    List<String> fields;
}
