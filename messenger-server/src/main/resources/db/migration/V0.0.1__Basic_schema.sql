create schema if not exists messenger;

create table messenger.t_user
(
    username   varchar(255) primary key,
    c_email    varchar(255) not null unique,
    c_password varchar(255) not null,
    c_role     varchar(50)  not null
);