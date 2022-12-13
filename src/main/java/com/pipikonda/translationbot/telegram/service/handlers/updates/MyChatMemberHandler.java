package com.pipikonda.translationbot.telegram.service.handlers.updates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.service.BotUserService;
import com.pipikonda.translationbot.telegram.dto.ChatMemberStatus;
import com.pipikonda.translationbot.telegram.TranslateBot;
import com.pipikonda.translationbot.telegram.dto.UpdateType;
import com.pipikonda.translationbot.telegram.view.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyChatMemberHandler implements UpdateHandler {

    private final BotUserService botUserService;
    private final MessageService messageService;
    private final TranslateBot translateBot;

    @Override
    @Transactional
    public void handleUpdate(Update update) throws TelegramApiException {
        ChatMemberStatus oldStatus = ChatMemberStatus.getByValue(update.getMyChatMember().getOldChatMember().getStatus());
        ChatMemberStatus newStatus = ChatMemberStatus.getByValue(update.getMyChatMember().getNewChatMember().getStatus());

        Long chatId = getChatId(update);
        log.info("User {} changed bot status from {} to {}", chatId, oldStatus, newStatus);
        if (newStatus == ChatMemberStatus.KICKED) {
            BotUser botUser = botUserService.getBotUserByChatId(chatId);
            botUserService.save(botUser.toBuilder()
                    .subscribed(false)
                    .lastUnsubscribedTime(Instant.now())
                    .userState(BotUser.UserState.LEFT)
                    .lastStateChanged(Instant.now())
                    .build());
        } else if (newStatus == ChatMemberStatus.MEMBER) {
            BotUser botUser = botUserService.getBotUserByChatId(chatId);
            botUserService.save(botUser.toBuilder()
                    .subscribed(true)
                    .lastSubscribedTime(Instant.now())
                    .userState(BotUser.UserState.ACTIVE)
                    .lastStateChanged(Instant.now())
                    .build());
            translateBot.execute(messageService.getMenuMessage(chatId, Locale.getDefault()));
        } else {
            log.warn("Handler is not present for status {}", newStatus);
        }
    }

    @Override
    public Long getChatId(Update update) {
        return update.getMyChatMember().getFrom().getId();
    }

    @Override
    public UpdateType getUpdateType() {
        return UpdateType.MY_CHAT_MEMBER;
    }
}
