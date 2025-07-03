create schema if not exists messenger;

create table messenger.t_user
(
    username   varchar(255) primary key,
    c_email    varchar(255) not null unique,
    c_password varchar(255) not null,
    c_role     varchar(50)  not null
);

create table messenger.t_chat
(
    id bigserial primary key
);

create table messenger.t_chat_participant
(
    id_chat  bigint       not null,
    username varchar(255) not null,
    constraint t_chat_participant_unique unique (id_chat, username),
    constraint t_chat_participant_chat_fk foreign key (id_chat)
        references messenger.t_chat (id) on delete cascade,
    constraint t_chat_participant_username_fk foreign key (username)
        references messenger.t_user (username) on delete cascade
);

create table messenger.t_message
(
    id           bigserial primary key,
    c_content    text      not null check (length(trim(c_content)) >= 1),
    c_created_at timestamp not null default current_timestamp,
    c_edited_at  timestamp
);

create table messenger.t_chat_message
(
    id_chat    bigint not null,
    id_message bigint not null,
    constraint t_chat_message_pk primary key (id_chat, id_message),
    constraint t_chat_message_chat_fk foreign key (id_chat)
        references messenger.t_chat (id) on delete cascade,
    constraint t_chat_message_message_fk foreign key (id_message)
        references messenger.t_message (id) on delete cascade
);

create table messenger.t_message_sender
(
    id_message bigint       not null,
    username   varchar(255) not null,
    constraint t_message_sender_pk primary key (id_message, username),
    constraint t_message_sender_message_fk foreign key (id_message)
        references messenger.t_message (id) on delete cascade,
    constraint t_message_sender_sender_fk foreign key (username)
        references messenger.t_user (username) on delete cascade
);