package com.pipikonda.translationbot.telegram;

import com.pipikonda.translationbot.telegram.service.UpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TelegramController {

    private final UpdateService updateService;

    @PostMapping("/callback/webhook")
    public void getWebhook(@RequestBody Update update) {
        updateService.handleUpdate(update);
    }
}
