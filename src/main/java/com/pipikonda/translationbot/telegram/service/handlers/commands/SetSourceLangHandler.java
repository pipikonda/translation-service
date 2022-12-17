package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.service.BotUserService;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.view.CallbackAnswerService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class SetSourceLangHandler implements CommandHandler {

    private final TranslateBot translateBot;
    private final MessageService messageService;
    private final CallbackAnswerService callbackAnswerService;
    private final BotUserService botUserService;

    @Override
    public void handleCommand(Update update, BotUser botUser, CallbackDataDto data) throws TelegramApiException {
        if (botUser.getUserState() == BotUser.UserState.SET_SOURCE_LANG) {
            botUserService.save(botUser.toBuilder()
                    .lastStateChanged(Instant.now())
                    .userState(BotUser.UserState.SET_TARGET_LANG)
                    .build());
            SendMessage sendMessage = messageService.setTargetLangMessage(botUser.getChatId(), Locale.getDefault());
            translateBot.execute(sendMessage);
            translateBot.execute(callbackAnswerService.getCallbackAnswer(update.getCallbackQuery().getId()));
        } else {
            log.info("Lang was not changed, user state is {} when expected SET_SOURCE_LANG", botUser.getUserState());
        }
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.SET_SOURCE_LANG;
    }
}
