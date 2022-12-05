create table translations
(
    id         bigserial primary key,
    text_value text unique
);

create table word_translations
(
    id                    bigserial primary key,
    source_translation_id bigint,
    target_translation_id bigint,
    source_lang           char(2),
    target_lang           char(2),
    user_id               varchar(50),
    constraint uni_source_to_target_translation unique (source_translation_id, target_translation_id, source_lang,
                                                        target_lang, user_id)
);

create table repeats
(
    id                  bigserial primary key,
    word_translation_id bigint,
    user_id             varchar(50),
    last_repeat         timestamp,
    next_repeat         timestamp,
    constraint uni_word_translation_user_id unique (user_id, word_translation_id)
);

create table repeat_attempts
(
    id             bigserial primary key,
    attempt_number int,
    repeat_id      bigint,
    is_success     boolean,
    attempt_time   timestamp,
    repeat_type    varchar(20),
    user_answer_id bigint
);

create table answers
(
    id                   bigserial primary key,
    translation_value_id bigint,
    repeat_attempt_id    bigint,
    is_correct           boolean
);

create table bot_users
(
    id                     bigserial primary key,
    chat_id                bigint,
    subscribed             boolean,
    user_state             varchar(20),
    last_state_changed     timestamp,
    last_subscribed_time   timestamp,
    last_unsubscribed_time timestamp
);