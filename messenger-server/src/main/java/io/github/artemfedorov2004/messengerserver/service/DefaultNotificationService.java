package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.PrivateChatPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.ChatMapper;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.MessageMapper;
import io.github.artemfedorov2004.messengerserver.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DefaultNotificationService implements NotificationService {

    private static final String PRIVATE_CHAT_NOTIFICATIONS_QUEUE = "/queue/private-chat-notifications";

    private static final String MESSAGE_NOTIFICATIONS_QUEUE = "/queue/message-notifications";

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    private final ChatMapper chatMapper;

    private final MessageMapper messageMapper;

    @Override
    public void send(User sender, Chat chat, PrivateChatNotification.Type notificationType) {
        User recipient = this.chatService.getOtherParticipantInPrivateChat(chat.getId(), sender);
        PrivateChatPayload payload = this.chatMapper.toPayload(chat, recipient);

        this.messagingTemplate.convertAndSendToUser(recipient.getUsername(), PRIVATE_CHAT_NOTIFICATIONS_QUEUE,
                new PrivateChatNotification(notificationType, payload));
    }

    @Override
    public void send(User sender, Message message, MessageNotification.Type notificationType) {
        List<String> recipients = this.chatService.getOtherParticipants(message.getChat().getId(), sender)
                .stream()
                .map(User::getUsername)
                .toList();
        MessagePayload payload = this.messageMapper.toPayload(message);

        for (String recipient : recipients) {
            this.messagingTemplate.convertAndSendToUser(recipient, MESSAGE_NOTIFICATIONS_QUEUE,
                    new MessageNotification(notificationType, payload));
        }
    }
}
