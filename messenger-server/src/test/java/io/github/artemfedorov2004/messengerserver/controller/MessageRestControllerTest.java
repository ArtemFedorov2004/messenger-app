package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.UpdateMessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.MessageMapper;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import io.github.artemfedorov2004.messengerserver.service.MessageService;
import io.github.artemfedorov2004.messengerserver.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.github.artemfedorov2004.messengerserver.entity.MessageNotification.Type.DELETE_MESSAGE;
import static io.github.artemfedorov2004.messengerserver.entity.MessageNotification.Type.EDIT_MESSAGE;
import static io.github.artemfedorov2004.messengerserver.entity.PrivateChatNotification.Type.EDIT_CHAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageRestControllerTest {

    @Mock
    MessageService messageService;

    @Mock
    NotificationService notificationService;

    @Mock
    MessageMapper messageMapper;

    @InjectMocks
    MessageRestController controller;

    @Test
    void getMessage_MessageExists_ReturnsMessage() {
        // given
        User principal = User.builder().username("user1").build();

        doReturn(new Message(
                1L,
                Chat.builder().id(1L).build(),
                User.builder().username("user1").build(),
                "Content",
                LocalDateTime.parse("2023-01-01T10:05:00"),
                LocalDateTime.parse("2023-01-02T10:05:00")
        ))
                .when(this.messageService).getMessage(1L);

        // when
        Message result = this.controller.getMessage(1L, principal);

        // then
        assertEquals(new Message(
                1L,
                Chat.builder().id(1L).build(),
                User.builder().username("user1").build(),
                "Content",
                LocalDateTime.parse("2023-01-01T10:05:00"),
                LocalDateTime.parse("2023-01-02T10:05:00")), result);

        verify(this.messageService).getMessage(1L);
        verifyNoMoreInteractions(this.messageService);
        verifyNoInteractions(this.notificationService, this.messageMapper);
    }

    @Test
    void getMessage_MessageDoesNotExist_ThrowsResourceNotFoundException() {
        // given
        Long messageId = 1L;
        User principal = User.builder().username("user1").build();

        doThrow(new ResourceNotFoundException("messenger_server.errors.message.not_found"))
                .when(this.messageService).getMessage(messageId);

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> this.controller.getMessage(messageId, principal));

        // then
        assertEquals("messenger_server.errors.message.not_found", exception.getMessage());
        verify(this.messageService).getMessage(messageId);
        verifyNoMoreInteractions(this.messageService);
        verifyNoInteractions(this.notificationService, this.messageMapper);
    }

    @Test
    void updateMessage_ValidRequest_ReturnsUpdatedMessage() throws BindException {
        // given
        Long messageId = 1L;
        User principal = User.builder().username("user1").build();
        UpdateMessagePayload payload = new UpdateMessagePayload("Updated content");
        Message updatedMessage = Message.builder()
                .id(messageId)
                .content("Updated content")
                .createdAt(LocalDateTime.parse("2023-01-01T10:05:00"))
                .editedAt(LocalDateTime.parse("2023-01-02T10:05:00"))
                .build();
        MessagePayload messagePayload = new MessagePayload(
                messageId,
                1L,
                "user1",
                "Updated content",
                LocalDateTime.parse("2023-01-01T10:05:00"),
                LocalDateTime.parse("2023-01-02T10:05:00"));

        doReturn(updatedMessage)
                .when(this.messageService).updateMessage(messageId, payload);
        doReturn(messagePayload)
                .when(this.messageMapper).toPayload(updatedMessage);

        // when
        var result = this.controller.updateMessage(
                messageId,
                principal,
                payload,
                new MapBindingResult(Map.of(), "payload")
        );

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(messagePayload, result.getBody());

        verify(this.messageService).updateMessage(messageId, payload);
        verify(this.messageMapper).toPayload(updatedMessage);
        verify(this.notificationService).send(principal, updatedMessage, EDIT_MESSAGE);
        verifyNoMoreInteractions(this.messageService, this.messageMapper, this.notificationService);
    }

    @Test
    void updateMessage_InvalidPayload_ThrowsBindException() {
        // given
        Long messageId = 1L;
        User principal = User.builder().username("user1").build();
        UpdateMessagePayload payload = new UpdateMessagePayload("");
        MapBindingResult bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "content", "Content is required"));

        // when
        var exception = assertThrows(BindException.class, () ->
                this.controller.updateMessage(messageId, principal, payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload",
                "content", "Content is required")), exception.getAllErrors());
        verifyNoInteractions(messageService, messageMapper, notificationService);
    }

    @Test
    void updateMessage_InvalidPayloadAndBindingResultIsBindException_ThrowsOriginalException() {
        // given
        Long messageId = 1L;
        User principal = User.builder().username("user1").build();
        UpdateMessagePayload payload = new UpdateMessagePayload("");
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "content", "Content is required"));

        // when
        var exception = assertThrows(BindException.class, () ->
                this.controller.updateMessage(messageId, principal, payload, bindingResult));

        // then
        assertEquals(List.of(new FieldError("payload",
                "content", "Content is required")), exception.getAllErrors());
        verifyNoInteractions(messageService, messageMapper, notificationService);
    }


    @Test
    void deleteMessage_ReturnsNoContent() {
        // given
        Long messageId = 1L;
        User principal = User.builder().username("user1").build();
        Message message = Message.builder()
                .id(messageId)
                .chat(Chat.builder().id(1L).build())
                .sender(User.builder().username("user1").build())
                .content("My message")
                .createdAt(LocalDateTime.parse("2023-01-01T10:05:00"))
                .editedAt(LocalDateTime.parse("2023-01-02T10:05:00"))
                .build();

        // when
        ResponseEntity<Void> result = this.controller.deleteMessage(message, principal);

        // then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.messageService).deleteMessage(messageId);
        verify(this.notificationService).send(principal, message.getChat(), EDIT_CHAT);
        verify(this.notificationService).send(principal, message, DELETE_MESSAGE);
        verifyNoMoreInteractions(this.messageService, this.notificationService);
        verifyNoInteractions(this.messageMapper);
    }
}