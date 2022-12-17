package com.pipikonda.translationbot.telegram.service.handlers.updates;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.dto.CallbackDataCommand;
import com.pipikonda.translationbot.telegram.dto.CallbackDataDto;
import com.pipikonda.translationbot.telegram.dto.UpdateType;
import com.pipikonda.translationbot.service.BotUserService;
import com.pipikonda.translationbot.telegram.service.handlers.CallbackDataMapper;
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
    private final Map<CallbackDataCommand, CommandHandler> commandHandlerMap;
    private final CallbackDataMapper callbackDataMapper;

    public CallbackQueryHandler(BotUserService botUserService,
                                List<CommandHandler> commandHandlerMap,
                                CallbackDataMapper callbackDataMapper) {
        this.botUserService = botUserService;
        this.commandHandlerMap = commandHandlerMap.stream()
                .collect(Collectors.toMap(CommandHandler::getCommand, Function.identity()));
        this.callbackDataMapper = callbackDataMapper;
    }

    @Override
    public void handleUpdate(Update update) {
        Long chatId = getChatId(update);
        BotUser botUser = botUserService.getBotUserByChatId(chatId);
        CallbackDataDto callbackDataDto = callbackDataMapper.stringToCallbackData(update.getCallbackQuery().getData());
        Optional.ofNullable(commandHandlerMap.get(callbackDataDto.getCommand()))
                .ifPresentOrElse(e -> {
                            try {
                                e.handleCommand(update, botUser, callbackDataDto);
                            } catch (TelegramApiException ex) {
                                log.error("CommandHandler got exception", ex);
                                throw new RuntimeException(ex);
                            }
                        },
                        () -> log.warn("Not found command handler for command " + callbackDataDto.getCommand()));
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
