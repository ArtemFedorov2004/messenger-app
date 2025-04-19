package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.communication.messengerserver.entity.ChatNotification.Type.NEW_CHAT;

@RequiredArgsConstructor
@Service
public class DefaultNotificationService implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    @Override
    public void send(MessageNotification notification) {
        Message message = notification.getMessage();
        String recipient = this.chatService.getMembersExcept(message.getChatId(), message.getSenderName())
                .getFirst();

        messagingTemplate.convertAndSendToUser(recipient, "/queue/notifications", notification);
    }

    // это потом надо убрать
    @Override
    public void send(User newUser) {
        List<Chat> allChats = chatService.getUserChats(newUser.getUsername());
        allChats.forEach(chat -> {
            String recipient = this.chatService.getMembersExcept(chat.getId(), newUser.getUsername())
                    .getFirst();

            var payload = chat.toPayload(recipient);

            messagingTemplate.convertAndSendToUser(recipient, "/queue/notifications", new ChatNotification(NEW_CHAT, payload));
        });
    }
}
