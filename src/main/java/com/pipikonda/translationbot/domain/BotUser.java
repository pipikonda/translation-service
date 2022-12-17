package com.pipikonda.translationbot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bot_users")
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private Boolean subscribed;

    @Enumerated(EnumType.STRING)
    private UserState userState;
    private Instant lastStateChanged;
    private Instant lastSubscribedTime;
    private Instant lastUnsubscribedTime;

    @Enumerated(EnumType.STRING)
    private Lang targetLang;

    @Enumerated(EnumType.STRING)
    private Lang sourceLang;

    public enum UserState {
        ACTIVE,
        LEFT,
        TRANSLATE_WORD,
        SET_SOURCE_LANG,
        SET_TARGET_LANG
    }
}
