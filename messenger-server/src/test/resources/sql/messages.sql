insert into messenger.t_message (id, c_content, c_created_at, c_edited_at)
values (1, 'Message 1', '2023-01-01 10:00:00', null),
       (2, 'Message 2', '2023-01-01 11:00:00', '2023-01-01 11:30:00');

alter sequence messenger.t_message_id_seq restart with 3;

insert into messenger.t_chat_message (id_chat, id_message)
values (1, 1),
       (1, 2);

insert into messenger.t_message_sender (id_message, username)
values (1, 'Artem'),
       (2, 'Dima');