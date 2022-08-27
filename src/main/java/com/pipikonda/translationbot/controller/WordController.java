package com.pipikonda.translationbot.controller;

import com.pipikonda.translationbot.domain.Word;
import com.pipikonda.translationbot.dto.WordCreateDto;
import com.pipikonda.translationbot.dto.WordResponseDto;
import com.pipikonda.translationbot.dto.WordTranslateDto;
import com.pipikonda.translationbot.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PostMapping("/api/user/word")
    public Word createWord(@Valid @RequestBody WordCreateDto dto) {
        return wordService.create(dto);
    }

    @PostMapping("/api/user/word/translate")
    public WordResponseDto translateWord(@Valid @RequestBody WordTranslateDto dto) {
        return wordService.translate(dto);
    }

}
