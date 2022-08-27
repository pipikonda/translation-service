package com.pipikonda.translationbot.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Lang {

    EN, RU, UK;

    public static final Lang DEFAULT_TARGET_LANG = EN;

    @JsonValue
    public String getJsonValue() {
        return name().toLowerCase();
    }
}
