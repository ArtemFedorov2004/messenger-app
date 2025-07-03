package io.github.artemfedorov2004.messengerserver.controller.payload.mapper;

import io.github.artemfedorov2004.messengerserver.controller.payload.PrivateChatPayload;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.service.ChatService;
import io.github.artemfedorov2004.messengerserver.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Component
public class ChatMapper {

    private final ChatService chatService;

    private final MessageService messageService;

    private final MessageMapper messageMapper;

    public PrivateChatPayload toPayload(Chat entity, User principal) {
        String participantName = this.chatService.getOtherParticipantInPrivateChat(entity.getId(), principal)
                .getUsername();

        Optional<Message> optionalMessage = this.messageService.findLastMessageByChatId(entity.getId());

        if (optionalMessage.isEmpty()) {
            return new PrivateChatPayload(
                    entity.getId(),
                    participantName,
                    null);
        }

        return new PrivateChatPayload(
                entity.getId(),
                participantName,
                this.messageMapper.toPayload(optionalMessage.get()));
    }

    public Iterable<PrivateChatPayload> toPayload(Iterable<Chat> entities, User principal) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map((chat) -> this.toPayload(chat, principal))
                .collect(Collectors.toList());
    }
}
