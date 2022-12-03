package com.pipikonda.translationbot.service.http;

import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * https://mymemory.translated.net/doc/spec.php
 * https://rapidapi.com/translated/api/mymemory-translation-memory/pricing
 */
@Service
@Slf4j
public class MyMemoryTranslateClient {

    private static final String DELIMITER = "|";
    private final String url;
    private final RestTemplate restTemplate;

    public MyMemoryTranslateClient(@Value("${service.get-translation.url}") String url,
                                   RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public List<String> getTranslation(Lang sourceLang, Lang targetLang, String text) {
        ResponseEntity<TranslationResponse> response =
                restTemplate.exchange(url, HttpMethod.GET, null, TranslationResponse.class, text, getLangPair(sourceLang, targetLang));
        log.info("External API translate response {}", response);
        return List.of(Optional.ofNullable(response.getBody())
                .filter(e -> e.getResponseStatus() == 200)
                .map(e -> e.getResponseData().getTranslatedText())
                .orElseThrow(() -> {
                    log.info("My memory response code is {} and body is {}", response.getBody().getResponseStatus(), response.getBody());
                    return new BasicLogicException(ErrorCode.UNKNOWN_ERROR, "Unexpected response from get-translation service");
                }));
    }

    private String getLangPair(Lang sourceLang, Lang targetLang) {
        return sourceLang + DELIMITER + targetLang;
    }

    @Builder
    @lombok.Value
    @Jacksonized
    public static class TranslationResponse {

        TranslationData responseData;
        Boolean quotaFinished; //if true... then get next token ?

        Long responseStatus;
        String responseDetails;
    }

    @Builder
    @lombok.Value
    @Jacksonized
    public static class TranslationData {

        String translatedText;
    }
}
