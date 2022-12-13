package com.pipikonda.translationbot.telegram.service.handlers.states;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ActiveStateHandler implements UserStateHandler {

    private static final String DEFAULT_START_COMMAND = "/start";
    private final TranslateBot translateBot;
    private final MessageService messageService;

    @Override
    public void handle(BotUser botUser, String messageText) throws TelegramApiException {
        if (DEFAULT_START_COMMAND.equals(messageText)) {
            SendMessage menuMessage = messageService.getMenuMessage(botUser.getChatId(), Locale.getDefault());
            translateBot.execute(menuMessage);
        }
    }

    @Override
    public BotUser.UserState getUserState() {
        return BotUser.UserState.ACTIVE;
    }
}
