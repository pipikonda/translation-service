package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.pipikonda.translationbot.domain.BotUser;
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
@RequiredArgsConstructor
@Slf4j
public class TranslateWordCommandHandler implements CommandHandler {

    private final BotUserService botUserService;
    private final TranslateBot translateBot;
    private final MessageService messageService;
    private final CallbackAnswerService callbackAnswerService;
    private final MessageSource messageSource;

    @Override
    public void handleCommand(Update update, BotUser botUser, CallbackDataDto data) throws TelegramApiException {
        botUser = botUser.toBuilder()
                .userState(BotUser.UserState.TRANSLATE_WORD)
                .lastStateChanged(Instant.now())
                .build();
        String[] params = new String[]{
                messageSource.getMessage("telegram.emoji.langs." + botUser.getSourceLang(), null, Locale.getDefault()),
                messageSource.getMessage("telegram.emoji.langs." + botUser.getTargetLang(), null, Locale.getDefault())
        };
        GetMessageBotRequestDto dto = GetMessageBotRequestDto.builder()
                .userLocale(Locale.getDefault())
                .chatId(botUser.getChatId())
                .messagePattern("telegram.message-text.translate-word")
                .params(params)
                .build();
        SendMessage message = messageService.getMessageWithBackKeyboard(dto);
        translateBot.execute(message);
        botUserService.save(botUser);
        translateBot.execute(callbackAnswerService.getCallbackAnswer(update.getCallbackQuery().getId()));
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.TRANSLATE_WORD;
    }
}
