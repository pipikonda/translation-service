package com.pipikonda.translationbot.telegram.service.handlers.updates;

import com.pipikonda.translationbot.domain.BotUser;
import com.pipikonda.translationbot.telegram.dto.ChatMemberStatus;
import com.pipikonda.translationbot.telegram.dto.ChatType;
import com.pipikonda.translationbot.telegram.dto.UpdateType;
import com.pipikonda.translationbot.telegram.service.BotUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyChatMemberHandler implements UpdateHandler {

    private final BotUserService botUserService;

    @Override
    @Transactional
    public void handleUpdate(Update update) {
        ChatMemberStatus oldStatus = ChatMemberStatus.getByValue(update.getMyChatMember().getOldChatMember().getStatus());
        ChatMemberStatus newStatus = ChatMemberStatus.getByValue(update.getMyChatMember().getNewChatMember().getStatus());
        ChatType chatType = ChatType.getByValue(update.getMyChatMember().getChat().getType());
        Long chatId = getChatId(update);
        log.info("User {} changed bot status from {} to {}, chat type is {}", chatId, oldStatus, newStatus, chatType);
        if (chatType != ChatType.PRIVATE) {
            return;
        }
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
