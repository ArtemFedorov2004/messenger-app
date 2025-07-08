package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.PrivateChatPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.ChatMapper;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.MessageMapper;
import io.github.artemfedorov2004.messengerserver.entity.*;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultNotificationServiceTest {

    @Mock
    SimpMessagingTemplate messagingTemplate;

    @Mock
    ChatService chatService;

    @Mock
    ChatMapper chatMapper;

    @Mock
    MessageMapper messageMapper;

    @InjectMocks
    DefaultNotificationService service;

    @Test
    void send_SendsToRecipient() {
        // given
        User sender = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(1L).build();
        User recipient = User.builder().username("user2").build();
        PrivateChatNotification.Type type = PrivateChatNotification.Type.NEW_CHAT;
        PrivateChatPayload payload = new PrivateChatPayload(1L, "user2", null);

        doReturn(recipient)
                .when(this.chatService).getOtherParticipantInPrivateChat(chat.getId(), sender);
        doReturn(payload)
                .when(this.chatMapper).toPayload(chat, recipient);

        // when
        this.service.send(sender, chat, type);

        // then
        verify(this.chatService).getOtherParticipantInPrivateChat(chat.getId(), sender);
        verify(this.chatMapper).toPayload(chat, recipient);
        verify(this.messagingTemplate).convertAndSendToUser(
                "user2",
                "/queue/private-chat-notifications",
                new PrivateChatNotification(type, payload)
        );
        verifyNoMoreInteractions(this.chatService, this.chatMapper, this.messagingTemplate);
    }

    @Test
    void send_ChatDoesNotExist_ThrowsResourceNotFoundException() {
        // given
        User sender = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(1L).build();

        doThrow(new ResourceNotFoundException("messenger_server.errors.chat_not_found"))
                .when(this.chatService).getOtherParticipantInPrivateChat(chat.getId(), sender);

        // when
        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.service.send(sender, chat, PrivateChatNotification.Type.NEW_CHAT));

        // then
        assertEquals("messenger_server.errors.chat_not_found", exception.getMessage());

        verify(this.chatService).getOtherParticipantInPrivateChat(chat.getId(), sender);
        verifyNoInteractions(this.chatMapper, this.messagingTemplate);
        verifyNoMoreInteractions(this.chatService);
    }

    @Test
    void send_SenderNotParticipant_ThrowsRuntimeException() {
        // given
        User sender = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(1L).build();

        doThrow(new RuntimeException("User user1 is not a participant of chat 1"))
                .when(this.chatService).getOtherParticipantInPrivateChat(chat.getId(), sender);

        // when
        var exception = assertThrows(RuntimeException.class,
                () -> this.service.send(sender, chat, PrivateChatNotification.Type.NEW_CHAT));

        // then
        assertEquals("User user1 is not a participant of chat 1", exception.getMessage());

        verify(this.chatService).getOtherParticipantInPrivateChat(chat.getId(), sender);
        verifyNoInteractions(this.chatMapper, this.messagingTemplate);
        verifyNoMoreInteractions(this.chatService);
    }

    @Test
    void send_SendsToAllRecipients() {
        // given
        User sender = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(1L).build();
        Message message = Message.builder()
                .id(1L)
                .chat(chat)
                .sender(sender)
                .content("Test message")
                .createdAt(LocalDateTime.parse("2015-08-04T10:11:30"))
                .editedAt(LocalDateTime.parse("2015-08-05T10:11:30"))
                .build();

        User recipient1 = User.builder().username("user2").build();
        User recipient2 = User.builder().username("user3").build();
        MessageNotification.Type type = MessageNotification.Type.NEW_MESSAGE;
        MessagePayload payload = new MessagePayload(1L, 1L, "user1", "Test message",
                LocalDateTime.parse("2015-08-04T10:11:30"), LocalDateTime.parse("2015-08-05T10:11:30"));

        doReturn(Set.of(recipient1, recipient2))
                .when(this.chatService).getOtherParticipants(chat.getId(), sender);
        doReturn(payload)
                .when(this.messageMapper).toPayload(message);

        // when
        this.service.send(sender, message, type);

        // then
        verify(this.chatService).getOtherParticipants(chat.getId(), sender);
        verify(this.messageMapper).toPayload(message);

        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MessageNotification> notificationCaptor = ArgumentCaptor.forClass(MessageNotification.class);

        verify(this.messagingTemplate, times(2)).convertAndSendToUser(
                usernameCaptor.capture(),
                eq("/queue/message-notifications"),
                notificationCaptor.capture()
        );

        assertThat(usernameCaptor.getAllValues())
                .containsExactlyInAnyOrder("user2", "user3");

        List<MessageNotification> notifications = notificationCaptor.getAllValues();
        assertEquals(2, notifications.size());
        notifications.forEach(n -> {
            assertEquals(type, n.getType());
            assertEquals(payload, n.getMessage());
        });

        verifyNoMoreInteractions(this.chatService, this.messageMapper, this.messagingTemplate);
    }

    @Test
    void send_SenderIsNotParticipantOfChat_ThrowsRuntimeException() {
        // given
        Chat chat = Chat.builder()
                .id(1L)
                .build();
        User sender = User.builder()
                .username("user1")
                .build();
        Message message = Message.builder()
                .id(1L)
                .chat(chat)
                .sender(sender)
                .content("Test message")
                .createdAt(LocalDateTime.parse("2015-08-04T10:11:30"))
                .editedAt(LocalDateTime.parse("2015-08-05T10:11:30"))
                .build();

        doThrow(new RuntimeException("User user1 is not a participant of chat 1"))
                .when(this.chatService).getOtherParticipants(1L, sender);

        // when
        var exception = assertThrows(RuntimeException.class,
                () -> this.service.send(sender, message, MessageNotification.Type.EDIT_MESSAGE));

        // then
        assertEquals("User user1 is not a participant of chat 1", exception.getMessage());

        verify(this.chatService).getOtherParticipants(1L, sender);
        verifyNoMoreInteractions(this.chatService);
        verifyNoInteractions(this.chatMapper, this.messagingTemplate, this.messageMapper);
    }
}