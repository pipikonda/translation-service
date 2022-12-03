package com.pipikonda.translationbot.controller;

import com.pipikonda.translationbot.controller.dto.CreateCustomTranslateDto;
import com.pipikonda.translationbot.controller.dto.CreateWordDto;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.dto.Response;
import com.pipikonda.translationbot.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PostMapping("/api/user/word")
    public Response<List<Translation>> translateWord(@Valid @RequestBody CreateWordDto dto) {
        return new Response<>(wordService.getTranslations(dto));
    }

    @PostMapping("/api/user/word/custom")
    public Response<String> createCustomTranslate(@Valid @RequestBody CreateCustomTranslateDto dto) {
        wordService.createCustomTranslate(dto);
        return Response.OK;
    }

}
