package com.pipikonda.translationbot.controller;

import com.pipikonda.translationbot.controller.dto.CreateRepeatAttemptDto;
import com.pipikonda.translationbot.controller.dto.CreateRepeatDto;
import com.pipikonda.translationbot.controller.dto.RepeatAttemptDto;
import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.dto.Response;
import com.pipikonda.translationbot.service.RepeatAttemptService;
import com.pipikonda.translationbot.service.RepeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RepeatController {

    private final RepeatService repeatService;
    private final RepeatAttemptService repeatAttemptService;

    @PostMapping("/api/user/repeat")
    public Response<Repeat> createRepeat(@Valid @RequestBody CreateRepeatDto dto) {
        return new Response<>(repeatService.createNewRepeat(dto));
    }

    @PostMapping("/api/user/repeat/attempt")
    public Response<RepeatAttemptDto> createRepeatAttempt(@Valid @RequestBody CreateRepeatAttemptDto dto) {
        return new Response<>(repeatAttemptService.createRepeatAttempt(dto.getRepeatId()));
    }
}
