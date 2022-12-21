package com.pipikonda.translationbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TranslationBotApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+2:00"));
        SpringApplication.run(TranslationBotApplication.class, args);
    }

}
