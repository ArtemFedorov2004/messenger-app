insert into messenger.t_chat (id)
values (1),
       (2);

alter sequence messenger.t_chat_id_seq restart with 3;

insert into messenger.t_chat_participant (id_chat, username)
values (1, 'Artem'),
       (1, 'Dima');

insert into messenger.t_chat_participant (id_chat, username)
values (2, 'Artem'),
       (2, 'Pavel');