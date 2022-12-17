package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.service.BotUserService;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.view.CallbackAnswerService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ChangeLangsHandler implements CommandHandler {

    private final TranslateBot translateBot;
    private final MessageService messageService;
    private final CallbackAnswerService callbackAnswerService;
    private final BotUserService botUserService;

    @Override
    public void handleCommand(Update update, BotUser botUser, CallbackDataDto data) throws TelegramApiException {
        SendMessage sendMessage = messageService.setSourceLangMessage(botUser.getChatId(), Locale.getDefault());
        botUserService.save(botUser.toBuilder()
                .userState(BotUser.UserState.SET_SOURCE_LANG)
                .lastStateChanged(Instant.now())
                .build());
        translateBot.execute(sendMessage);
        translateBot.execute(callbackAnswerService.getCallbackAnswer(update.getCallbackQuery().getId()));
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.CHANGE_LANGS;
    }
}
