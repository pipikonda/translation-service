package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.GetMessageBotRequestDto;
import com.pipikonda.translationbot.telegram.service.BotUserService;
import com.pipikonda.translationbot.telegram.view.CallbackAnswerService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BotInfoHandler implements CommandHandler {

    private final MessageService messageService;
    private final TranslateBot translateBot;
    private final BotUserService botUserService;
    private final CallbackAnswerService callbackAnswerService;

    @Override
    public void handleCommand(String queryId, BotUser botUser) throws TelegramApiException, JsonProcessingException {
        botUserService.save(botUser.toBuilder()
                .userState(BotUser.UserState.ACTIVE)
                .lastStateChanged(Instant.now())
                .build());
        SendMessage sendMessage = messageService.getMessageWithBackKeyboard(GetMessageBotRequestDto.builder()
                        .chatId(botUser.getChatId())
                        .messagePattern("telegram.message-text.bot-info")
                        .userLocale(Locale.getDefault())
                .build());
        translateBot.execute(sendMessage);
        translateBot.execute(callbackAnswerService.getCallbackAnswer(queryId));
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.GET_BOT_INFO;
    }
}
