create table words (
    id bigserial,
    translation_id bigint
);

create table translation_info (
    id bigserial,
    type varchar(40),
    user_id varchar(50)
);

create table translations (
    id bigserial,
    translation_id bigint,
    value text,
    lang char(2)
);