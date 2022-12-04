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
    user_id               varchar(50)
);

create table repeats
(
    id                      bigserial primary key,
    word_translation_id     bigint,
    user_id                 varchar(50),
    last_repeat             timestamp,
    next_repeat             timestamp
);

create table repeat_attempts
(
    id             bigserial primary key,
    attempt_number int,
    repeat_id      bigint,
    is_success     boolean,
    attempt_time   timestamp,
    repeat_type    varchar(20),
    user_answer    text
);

create table answers
(
    id                bigserial primary key,
    text_value        bigint,
    repeat_attempt_id bigint,
    is_correct        boolean
);