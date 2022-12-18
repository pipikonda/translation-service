package com.pipikonda.translationbot.telegram.service.handlers.commands;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.error.ClientErrorException;
import com.pipikonda.translationbot.service.WordService;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.dto.GetMessageBotRequestDto;
import com.pipikonda.translationbot.telegram.view.CallbackAnswerService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GetRandomWordHandler implements CommandHandler {

    private final WordService wordService;
    private final TranslateBot translateBot;
    private final CallbackAnswerService callbackAnswerService;
    private final MessageService messageService;

    @Override
    public void handleCommand(Update update, BotUser botUser, CallbackDataDto data) throws TelegramApiException {
        SendMessage randomWordPoll;
        try {
            randomWordPoll = wordService.getRandomWordPoll(botUser, botUser.getSourceLang(), botUser.getTargetLang());
            translateBot.execute(randomWordPoll);
        } catch (ClientErrorException exception) {
            translateBot.execute(messageService.getMessageWithBackKeyboard(GetMessageBotRequestDto.builder()
                    .userLocale(Locale.getDefault())
                    .chatId(botUser.getChatId())
                    .messagePattern("telegram.errors.random-word.not-found-words")
                    .build()));
        }
        translateBot.execute(callbackAnswerService.getCallbackAnswer(update.getCallbackQuery().getId()));
    }

    @Override
    public CallbackDataCommand getCommand() {
        return CallbackDataCommand.GET_RANDOM_WORD;
    }
}
