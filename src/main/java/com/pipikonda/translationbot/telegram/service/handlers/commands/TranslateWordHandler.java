package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.dto.GetMessageBotRequestDto;
import com.pipikonda.translationbot.telegram.service.BotUserService;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.view.CallbackAnswerService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslateWordHandler implements CommandHandler {

    private final BotUserService botUserService;
    private final TranslateBot translateBot;
    private final MessageService messageService;
    private final CallbackAnswerService callbackAnswerService;

    @Override
    public void handleCommand(String queryId, BotUser botUser) throws TelegramApiException, JsonProcessingException {
        botUser = botUser.toBuilder()
                .userState(BotUser.UserState.TRANSLATE_WORD)
                .lastStateChanged(Instant.now())
                .build();
        GetMessageBotRequestDto dto = GetMessageBotRequestDto.builder()
                .userLocale(Locale.getDefault())
                .chatId(botUser.getChatId())
                .messagePattern("telegram.message-text.translate-word")
                .build();
        SendMessage message = messageService.getMessageWithBackKeyboard(dto);
        translateBot.execute(message);
        botUserService.save(botUser);
        translateBot.execute(callbackAnswerService.getCallbackAnswer(queryId));
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.TRANSLATE_WORD;
    }
}
