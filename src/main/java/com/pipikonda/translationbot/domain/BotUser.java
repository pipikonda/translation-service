package com.pipikonda.translationbot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
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
@Table(name = "users")
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private Boolean subscribed;
    private UserState userState;
    private Instant lastStateChanged;
    private Instant lastSubscribedTime;
    private Instant lastUnsubscribedTime;

    public enum UserState {
        ACTIVE,
        LEFT,
        TRANSLATE_WORD
    }
}
