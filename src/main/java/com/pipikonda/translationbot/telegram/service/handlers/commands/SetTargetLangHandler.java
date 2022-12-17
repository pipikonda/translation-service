package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.service.BotUserService;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.dto.GetMessageBotRequestDto;
import com.pipikonda.translationbot.telegram.view.CallbackAnswerService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class SetTargetLangHandler implements CommandHandler {

    private final TranslateBot translateBot;
    private final MessageService messageService;
    private final CallbackAnswerService callbackAnswerService;
    private final BotUserService botUserService;
    private final MessageSource messageSource;

    @Override
    public void handleCommand(Update update, BotUser botUser, CallbackDataDto data) throws TelegramApiException {
        if (botUser.getUserState() == BotUser.UserState.SET_TARGET_LANG) {
            Lang source = Lang.valueOf(data.getParams().get("source").asText());
            Lang target = Lang.valueOf(data.getParams().get("target").asText());
            botUser = botUser.toBuilder()
                    .userState(BotUser.UserState.ACTIVE)
                    .lastStateChanged(Instant.now())
                    .build();
            botUserService.changeLangs(botUser, source, target);
            Object[] params = new String[]{
                    messageSource.getMessage("telegram.emoji.langs." + source.name(), null, Locale.getDefault()),
                    messageSource.getMessage("telegram.emoji.langs." + target.name(), null, Locale.getDefault()),
            };
            SendMessage sendMessage = messageService.getMessageWithBackKeyboard(GetMessageBotRequestDto.builder()
                    .chatId(botUser.getChatId())
                    .userLocale(Locale.getDefault())
                    .messagePattern("telegram.message-text.lang-changed")
                    .params(params)
                    .build());
            translateBot.execute(sendMessage);
            translateBot.execute(callbackAnswerService.getCallbackAnswer(update.getCallbackQuery().getId()));
        } else {
            log.info("Lang was not changed, user state is {}", botUser.getUserState());
        }
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.SET_TARGET_LANG;
    }
}
