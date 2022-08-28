create table words
(
    id             bigserial primary key
);

create table translations
(
    id      bigserial primary key,
    word_id bigint,
    value   text,
    lang    char(2),
    user_id varchar(50)
);

create table repeats
(
    id          bigserial primary key,
    word_id     bigint,
    user_id     varchar(50),
    source_lang char(2),
    target_lang char(2),
    last_repeat timestamp,
    next_repeat timestamp
);

create table repeat_attempts
(
    id             bigserial primary key,
    attempt_number int,
    repeat_id      bigint,
    is_success     boolean,
    attempt_time   timestamp,
    repeat_type    varchar(20)
);