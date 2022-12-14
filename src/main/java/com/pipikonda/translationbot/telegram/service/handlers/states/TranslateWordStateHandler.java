package com.pipikonda.translationbot.telegram.service.handlers.states;

import com.pipikonda.translationbot.controller.dto.CreateWordDto;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.service.BotUserService;
import com.pipikonda.translationbot.service.WordService;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.GetMessageBotRequestDto;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TranslateWordStateHandler implements UserStateHandler {

    private final WordService wordService;
    private final TranslateBot translateBot;
    private final MessageService messageService;
    private final BotUserService botUserService;
    private final MessageSource messageSource;

    @Override
    public void handle(BotUser botUser, String messageText) throws TelegramApiException {
        CreateWordDto dto = CreateWordDto.builder()
                .sourceLang(botUser.getSourceLang())
                .targetLang(botUser.getTargetLang())
                .userId(String.valueOf(botUser.getId()))
                .word(messageText)
                .build();
        List<String> translations = wordService.getTranslations(dto)
                .stream()
                .map(Translation::getTextValue)
                .map(StringUtils::capitalize)
                .toList();

        String[] params = new String[]{dto.getWord(),
                String.join("\n", translations),
                messageSource.getMessage("telegram.emoji.langs." + botUser.getSourceLang(), null, Locale.getDefault()),
                messageSource.getMessage("telegram.emoji.langs." + botUser.getTargetLang(), null, Locale.getDefault())};
        GetMessageBotRequestDto botRequestDto = GetMessageBotRequestDto.builder()
                .userLocale(Locale.getDefault())
                .chatId(botUser.getChatId())
                .params(params)
                .messagePattern("telegram.message-text.translate-word-result")
                .build();
        SendMessage message = messageService.getMessageWithBackKeyboard(botRequestDto);
        translateBot.execute(message);
        botUserService.save(botUser.toBuilder()
                .userState(BotUser.UserState.TRANSLATE_WORD)
                .lastStateChanged(Instant.now())
                .build());
    }

    @Override
    public BotUser.UserState getUserState() {
        return BotUser.UserState.TRANSLATE_WORD;
    }
}
