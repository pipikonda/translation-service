package com.pipikonda.translationbot.controller.dto;

import lombok.Data;

@Data
public class Response<T> {

    public static final Response<String> OK = new Response<>("OK");
    private final T result;

    public Response(T result) {
        this.result = result;
    }
}
