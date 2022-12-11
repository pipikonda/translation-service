package com.pipikonda.translationbot.telegram.service.handlers.states;

import com.pipikonda.translationbot.controller.dto.CreateWordDto;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.domain.Lang;
import com.pipikonda.translationbot.domain.Translation;
import com.pipikonda.translationbot.service.WordService;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.GetMessageBotRequestDto;
import com.pipikonda.translationbot.telegram.service.BotUserService;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void handle(BotUser botUser, String word) throws TelegramApiException {
        CreateWordDto dto = CreateWordDto.builder()
                .sourceLang(Lang.EN)
                .targetLang(Lang.RU)
                .userId(String.valueOf(botUser.getId()))
                .word(word)
                .build();
        List<String> translations = wordService.getTranslations(dto)
                .stream()
                .map(Translation::getTextValue)
                .map(StringUtils::capitalize)
                .toList();

        GetMessageBotRequestDto botRequestDto = GetMessageBotRequestDto.builder()
                .userLocale(Locale.getDefault())
                .chatId(botUser.getChatId())
                .params(new String[]{dto.getWord(), String.join("\n", translations), Lang.EN.name(), Lang.RU.name()})
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
