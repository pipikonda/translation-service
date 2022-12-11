package com.pipikonda.translationbot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Component
@Slf4j
public class TranslateBot extends SpringWebhookBot {

    public TranslateBot() {
        super(SetWebhook.builder()
                .url("https://7e47-46-98-123-26.eu.ngrok.io/callback/webhook")
                .secretToken("5229575811:AAGQv2iROz1QHZYLKp2RnyadVN-UGJhARNI")
                .build());
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("Got update to bot ===> {}", update);
        return null;
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
        return "5229575811:AAGQv2iROz1QHZYLKp2RnyadVN-UGJhARNI";
    }

}
