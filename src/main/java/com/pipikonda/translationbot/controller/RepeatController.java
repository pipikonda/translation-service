package com.pipikonda.translationbot.controller;

import com.pipikonda.translationbot.controller.dto.CreateRepeatAttemptDto;
import com.pipikonda.translationbot.controller.dto.CreateRepeatDto;
import com.pipikonda.translationbot.controller.dto.RepeatAttemptDto;
import com.pipikonda.translationbot.controller.dto.SaveRepeatAnswerDto;
import com.pipikonda.translationbot.domain.Repeat;
import com.pipikonda.translationbot.controller.dto.Response;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.service.RepeatAttemptService;
import com.pipikonda.translationbot.service.RepeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RepeatController {

    private final RepeatService repeatService;
    private final RepeatAttemptService repeatAttemptService;

    @PostMapping("/api/user/repeat")
    public Response<Repeat> createRepeat(@Valid @RequestBody CreateRepeatDto dto) {
        if (repeatService.checkRepeatPresent(dto.getUserId(), dto.getWordTranslationId())) {
            throw new BasicLogicException(ErrorCode.BAD_REQUEST,
                    "Repeat with userId " + dto.getUserId() + " and  wordTranslationId " +
                            dto.getWordTranslationId() + " is already exists");
        }
        return new Response<>(repeatService.createNewRepeat(dto));
    }

    @PostMapping("/api/user/repeat/attempt")
    public Response<RepeatAttemptDto> createRepeatAttempt(@Valid @RequestBody CreateRepeatAttemptDto dto) {
        Repeat repeat = repeatService.findById(dto.getRepeatId());
        return new Response<>(repeatAttemptService.createRepeatAttempt(repeat));
    }

    @PostMapping("/api/user/repeat/attempt/answer")
    public Response<Boolean> answer(@Valid @RequestBody SaveRepeatAnswerDto dto) {
        return new Response<>(repeatAttemptService.saveAnswer(dto.getRepeatAttemptId(), dto.getAnswer()));
    }
}
