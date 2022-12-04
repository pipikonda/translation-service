package com.pipikonda.translationbot.telegram;

import com.pipikonda.translationbot.telegram.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Component
@Slf4j
public class TranslateBot extends SpringWebhookBot {

    private final UpdateService updateService;

    public TranslateBot(UpdateService updateService) {
        super(SetWebhook.builder()
                .url("")
                .secretToken("token")
                .build());
        this.updateService = updateService;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        updateService.handleUpdate(update);
        return null;
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {

        //?? ?
    }

    @Override
    public String getBotPath() {
        return null;
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return null;
    }
}
