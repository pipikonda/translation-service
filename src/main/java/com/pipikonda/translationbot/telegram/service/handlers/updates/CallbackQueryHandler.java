package com.pipikonda.translationbot.telegram.service.handlers.updates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.service.BotUserService;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.dto.UpdateType;
import com.pipikonda.translationbot.telegram.service.handlers.commands.CommandHandler;
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
public class CallbackQueryHandler implements UpdateHandler {

    private final BotUserService botUserService;
    private final ObjectMapper objectMapper;
    private final Map<CallbackDataCommand, CommandHandler> commandHandlerMap;

    public CallbackQueryHandler(BotUserService botUserService,
                                ObjectMapper objectMapper,
                                List<CommandHandler> commandHandlerMap) {
        this.botUserService = botUserService;
        this.objectMapper = objectMapper;
        this.commandHandlerMap = commandHandlerMap.stream()
                .collect(Collectors.toMap(CommandHandler::getCommand, Function.identity()));
    }

    @Override
    public void handleUpdate(Update update) throws JsonProcessingException {
        Long chatId = getChatId(update);
        BotUser botUser = botUserService.getBotUserByChatId(chatId);
        String callbackData = update.getCallbackQuery().getData();
        CallbackDataDto callbackDataDto = objectMapper.readValue(callbackData, CallbackDataDto.class);
        Optional.ofNullable(commandHandlerMap.get(callbackDataDto.getValue()))
                .ifPresentOrElse(e -> {
                            try {
                                e.handleCommand(update.getCallbackQuery().getId(), botUser);
                            } catch (TelegramApiException | JsonProcessingException ex) {
                                log.error("CommandHandler got exception", ex);
                                throw new RuntimeException(ex);
                            }
                        },
                        () -> log.warn("Not found command handler for command " + callbackDataDto.getValue()));
    }

    @Override
    public Long getChatId(Update update) {
        return update.getCallbackQuery().getFrom().getId();
    }

    @Override
    public UpdateType getUpdateType() {
        return UpdateType.CALLBACK_QUERY;
    }
}
