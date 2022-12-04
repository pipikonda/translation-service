package com.pipikonda.translationbot.telegram.service.handlers.updates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.service.BotUserService;
import com.pipikonda.translationbot.telegram.dto.UpdateType;
import com.pipikonda.translationbot.telegram.service.handlers.states.UserStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageHandler implements UpdateHandler {

    private final Map<BotUser.UserState, UserStateHandler> stateHandlerMap;
    private final BotUserService botUserService;

    public MessageHandler(List<UserStateHandler> handlers, BotUserService botUserService) {
        this.stateHandlerMap = handlers.stream()
                .collect(Collectors.toMap(UserStateHandler::getUserState, Function.identity()));
        this.botUserService = botUserService;
    }

    @Override
    public void handleUpdate(Update update) {
        Long chatId = getChatId(update);
        BotUser botUser = botUserService.getBotUserByChatId(chatId);
        Optional.ofNullable(stateHandlerMap.get(botUser.getUserState()))
                .ifPresentOrElse(e -> {
                            try {
                                e.handle(botUser, update.getMessage().getText());
                            } catch (TelegramApiException | JsonProcessingException ex) {
                                log.error("Handle user state message got exception", ex);
                                throw new RuntimeException(ex);
                            }
                        },
                        () -> log.warn("Not found user state handler for {}", botUser.getUserState()));
    }

    @Override
    public Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    @Override
    public UpdateType getUpdateType() {
        return UpdateType.MESSAGE;
    }
}
