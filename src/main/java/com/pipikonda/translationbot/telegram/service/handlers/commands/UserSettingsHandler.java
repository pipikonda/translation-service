package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.service.TimePeriodService;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.dto.GetSettingsInfoDto;
import com.pipikonda.translationbot.telegram.view.CallbackAnswerService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSettingsHandler implements CommandHandler {

    private final TranslateBot translateBot;
    private final MessageService messageService;
    private final TimePeriodService timePeriodService;
    private final CallbackAnswerService callbackAnswerService;

    @Override
    public void handleCommand(Update update, BotUser botUser, CallbackDataDto data) throws TelegramApiException {
        String periods = timePeriodService.findByUserId(botUser.getId())
                .stream()
                .map(e -> e.getStartTime() + " - " + e.getEndTime())
                .collect(Collectors.joining("\n"));
        Object[] params = new Object[]{
                "telegram.emoji.langs." + botUser.getSourceLang(),
                "telegram.emoji.langs." + botUser.getTargetLang(),
                periods
        };
        GetSettingsInfoDto dto = GetSettingsInfoDto.builder()
                .userLocale(Locale.getDefault())
                .chatId(botUser.getChatId())
                .params(params)
                .build();
        SendMessage settingsMessage = messageService.getSettingsMessage(dto);
        translateBot.execute(settingsMessage);
        translateBot.execute(callbackAnswerService.getCallbackAnswer(update.getCallbackQuery().getId()));
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.USER_SETTINGS;
    }
}
