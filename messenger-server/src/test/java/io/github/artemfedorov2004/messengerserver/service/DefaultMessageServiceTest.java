package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.controller.payload.NewMessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.UpdateMessagePayload;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import io.github.artemfedorov2004.messengerserver.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultMessageServiceTest {

    @Mock
    MessageRepository messageRepository;

    @InjectMocks
    DefaultMessageService service;

    @Test
    void createMessage_ReturnsCreatedMessage() {
        // given
        Chat chat = Chat.builder()
                .id(1L)
                .build();
        User sender = User.builder()
                .username("user 1")
                .build();
        NewMessagePayload payload = new NewMessagePayload("content");

        Message savedMessage = new Message(1L, chat, sender, "content",
                LocalDateTime.parse("2015-08-04T10:11:30"), null);

        doReturn(savedMessage)
                .when(this.messageRepository).save(any(Message.class));

        // when
        Message result = this.service.createMessage(chat, sender, payload);

        // then
        assertNotNull(result.getCreatedAt());
        assertEquals(savedMessage, result);
        verify(this.messageRepository).save(any(Message.class));
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void getByChatId_ReturnsMessagesPage() {
        // given
        Long chatId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Message> messages = List.of(
                new Message(
                        1L,
                        Chat.builder().id(1L).build(),
                        User.builder().username("username").build(),
                        "Message 1",
                        LocalDateTime.parse("2015-08-04T10:11:30"),
                        null),
                new Message(
                        2L,
                        Chat.builder().id(1L).build(),
                        User.builder().username("username").build(),
                        "Message 2",
                        LocalDateTime.parse("2015-08-04T10:11:30"),
                        null)
        );
        Page<Message> expectedPage = new PageImpl<>(messages, pageable, 2);

        doReturn(expectedPage)
                .when(this.messageRepository).findByChatId(chatId, pageable);

        // when
        Page<Message> result = this.service.getByChatId(chatId, pageable);

        // then
        assertEquals(expectedPage, result);
        verify(this.messageRepository).findByChatId(chatId, pageable);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void updateMessage_UpdatesContentAndEditedAt() {
        // given
        UpdateMessagePayload payload = new UpdateMessagePayload("Updated content");
        Message message = new Message(
                1L,
                Chat.builder().id(1L).build(),
                User.builder().username("username").build(),
                "Old content",
                LocalDateTime.parse("2015-08-04T10:11:30"),
                null);
        Message updatedMessage = new Message(
                1L,
                Chat.builder().id(1L).build(),
                User.builder().username("username").build(),
                "Updated content",
                LocalDateTime.parse("2015-08-04T10:11:30"),
                LocalDateTime.parse("2025-08-04T10:11:30"));

        doReturn(Optional.of(message))
                .when(this.messageRepository).findById(1L);

        // when
        Message result = this.service.updateMessage(1L, payload);

        // then
        assertEquals("Updated content", result.getContent());
        assertNotNull(result.getEditedAt());
        verify(this.messageRepository).findById(1L);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void updateMessage_MessageNotFound_ThrowsResourceNotFoundException() {
        // given
        Long messageId = 1L;
        UpdateMessagePayload payload = new UpdateMessagePayload("New content");

        doReturn(Optional.empty())
                .when(this.messageRepository).findById(1L);

        // when
        var exception = assertThrows(ResourceNotFoundException.class, () ->
                this.service.updateMessage(messageId, payload));

        // then
        assertEquals("messenger_server.errors.message.not_found", exception.getMessage());
        verify(this.messageRepository).findById(messageId);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void getMessage_MessageExists_ReturnsMessage() {
        // given
        Message message = new Message(
                1L,
                Chat.builder().id(1L).build(),
                User.builder().username("username").build(),
                "Old content",
                LocalDateTime.parse("2015-08-04T10:11:30"),
                null);

        doReturn(Optional.of(message))
                .when(this.messageRepository).findById(1L);

        // when
        Message result = service.getMessage(1L);

        // then
        assertEquals(message, result);
        verify(this.messageRepository).findById(1L);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void getMessage_MessageDoesNotExist_ThrowsResourceNotFoundException() {
        // given

        doReturn(Optional.empty())
                .when(this.messageRepository).findById(1L);

        // when
        var exception = assertThrows(ResourceNotFoundException.class, () ->
                this.service.getMessage(1L));

        // then
        assertEquals("messenger_server.errors.message.not_found", exception.getMessage());
        verify(this.messageRepository).findById(1L);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void deleteMessage_DeletesById() {
        // given
        Long messageId = 1L;

        // when
        this.service.deleteMessage(messageId);

        // then
        verify(this.messageRepository).deleteById(messageId);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void canGetMessage_WhenUserIsParticipant_ReturnsTrue() {
        // given
        Long messageId = 1L;
        User user = new User("user1", "email", "pass", Role.ROLE_USER);

        doReturn(true)
                .when(this.messageRepository).existsByIdAndChatParticipantsContaining(messageId, user);

        // when
        boolean result = this.service.canGetMessage(messageId, user);

        // then
        assertTrue(result);
        verify(this.messageRepository).existsByIdAndChatParticipantsContaining(messageId, user);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void canGetMessage_WhenUserNotParticipant_ReturnsFalse() {
        // given
        Long messageId = 1L;
        User user = new User("user1", "email", "pass", Role.ROLE_USER);

        doReturn(false)
                .when(this.messageRepository).existsByIdAndChatParticipantsContaining(messageId, user);

        // when
        boolean result = this.service.canGetMessage(messageId, user);

        // then
        assertFalse(result);
        verify(this.messageRepository).existsByIdAndChatParticipantsContaining(messageId, user);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void canEditMessage_WhenUserIsSender_ReturnsTrue() {
        // given
        Long messageId = 1L;
        User user = new User("user1", "email", "pass", Role.ROLE_USER);

        doReturn(true)
                .when(this.messageRepository).existsByIdAndSender(messageId, user);

        // when
        boolean result = this.service.canEditMessage(messageId, user);

        // then
        assertTrue(result);
        verify(this.messageRepository).existsByIdAndSender(messageId, user);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void canDeleteMessage_WhenUserIsSender_ReturnsTrue() {
        // given
        Long messageId = 1L;
        User user = new User("user1", "email", "pass", Role.ROLE_USER);

        doReturn(true)
                .when(this.messageRepository).existsByIdAndSender(messageId, user);

        // when
        boolean result = this.service.canDeleteMessage(messageId, user);

        // then
        assertTrue(result);
        verify(this.messageRepository).existsByIdAndSender(messageId, user);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void findLastMessageByChatId_MessageExists_ReturnsMessage() {
        // given
        Long chatId = 1L;
        Message expectedMessage = Message.builder()
                .id(1L)
                .content("Last message")
                .createdAt(LocalDateTime.now())
                .build();

        doReturn(Optional.of(expectedMessage))
                .when(this.messageRepository).findLastMessageByChatId(chatId);

        // when
        Optional<Message> result = this.service.findLastMessageByChatId(chatId);

        // then
        assertTrue(result.isPresent());
        assertEquals(expectedMessage, result.get());
        verify(this.messageRepository).findLastMessageByChatId(chatId);
        verifyNoMoreInteractions(this.messageRepository);
    }

    @Test
    void findLastMessageByChatId_NoMessages_ReturnsEmptyOptional() {
        // given
        Long chatId = 1L;

        doReturn(Optional.empty())
                .when(this.messageRepository).findLastMessageByChatId(chatId);

        // when
        Optional<Message> result = this.service.findLastMessageByChatId(chatId);

        // then
        assertFalse(result.isPresent());
        verify(this.messageRepository).findLastMessageByChatId(chatId);
        verifyNoMoreInteractions(this.messageRepository);
    }
}