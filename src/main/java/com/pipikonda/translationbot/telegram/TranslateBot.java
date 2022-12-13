package com.pipikonda.translationbot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Component
@Slf4j
public class TranslateBot extends SpringWebhookBot {

    private final String botToken;

    public TranslateBot(@Value("${service.telegram.bot.token}") String botToken,
                        @Value("${service.telegram.bot.webhook-url}") String webhookUrl) {
        super(SetWebhook.builder()
                .url(webhookUrl)
                .secretToken(botToken)
                .build());
        this.botToken = botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
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
        return botToken;
    }

}
