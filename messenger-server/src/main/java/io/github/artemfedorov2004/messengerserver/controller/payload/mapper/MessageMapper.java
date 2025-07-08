package io.github.artemfedorov2004.messengerserver.controller.payload.mapper;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class MessageMapper implements Mappable<Message, MessagePayload> {
    @Override
    public Message fromPayload(MessagePayload payload) {
        return Message.builder()
                .id(payload.id())
                .chat(new Chat(payload.chatId(), new HashSet<>()))
                .sender(User.builder()
                        .username(payload.senderName())
                        .build()
                )
                .content(payload.content())
                .createdAt(payload.createdAt())
                .editedAt(payload.editedAt())
                .build();
    }

    @Override
    public MessagePayload toPayload(Message entity) {
        return new MessagePayload(
                entity.getId(),
                entity.getChat().getId(),
                entity.getSender().getUsername(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getEditedAt()
        );
    }

    @Override
    public Iterable<MessagePayload> toPayload(Iterable<Message> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
