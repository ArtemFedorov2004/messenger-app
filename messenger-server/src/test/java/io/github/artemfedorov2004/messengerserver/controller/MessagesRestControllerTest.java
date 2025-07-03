package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.NewMessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.MessageMapper;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import io.github.artemfedorov2004.messengerserver.service.ChatService;
import io.github.artemfedorov2004.messengerserver.service.MessageService;
import io.github.artemfedorov2004.messengerserver.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.github.artemfedorov2004.messengerserver.entity.MessageNotification.Type.NEW_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MessagesRestControllerTest {

    @Mock
    MessageService messageService;

    @Mock
    ChatService chatService;

    @Mock
    MessageMapper messageMapper;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    MessagesRestController controller;

    @Test
    void getChat_ChatExists_ReturnsChat() {
        // given
        Long chatId = 1L;
        Chat expectedChat = Chat.builder()
                .id(chatId)
                .build();

        doReturn(expectedChat)
                .when(this.chatService).getChat(chatId);

        // when
        Chat result = this.controller.getChat(chatId);

        // then
        assertEquals(expectedChat, result);
        verify(this.chatService).getChat(chatId);
        verifyNoMoreInteractions(this.chatService);
        verifyNoInteractions(this.messageService, this.messageMapper, this.notificationService);
    }

    @Test
    void getChat_ChatDoesNotExist_ThrowsResourceNotFoundException() {
        // given
        Long nonExistentChatId = 999L;

        doThrow(new ResourceNotFoundException("messenger_server.errors.chat.not_found"))
                .when(this.chatService).getChat(nonExistentChatId);

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> this.controller.getChat(nonExistentChatId));

        // then
        assertEquals("messenger_server.errors.chat.not_found", exception.getMessage());
        verify(this.chatService).getChat(nonExistentChatId);
        verifyNoMoreInteractions(this.chatService);
        verifyNoInteractions(this.messageService, this.messageMapper, this.notificationService);
    }

    @Test
    void getMessages_ReturnsPaginatedMessages() {
        // given
        Long chatId = 1L;
        User principal = User.builder().username("user1").build();
        Pageable pageable = PageRequest.of(0, 20,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Message> messagePage = new PageImpl<>(
                List.of(Message.builder()
                                .id(1L)
                                .chat(Chat.builder().id(chatId).build())
                                .sender(User.builder().username("User 1").build())
                                .content("Message 1")
                                .createdAt(LocalDateTime.parse("2023-01-01T10:05:00"))
                                .editedAt(LocalDateTime.parse("2023-01-02T10:05:00"))
                                .build(),
                        Message.builder()
                                .id(2L)
                                .chat(Chat.builder().id(chatId).build())
                                .sender(User.builder().username("User 2").build())
                                .content("Message 2")
                                .createdAt(LocalDateTime.parse("2023-02-01T10:05:00"))
                                .editedAt(LocalDateTime.parse("2023-02-02T10:05:00"))
                                .build()), pageable, 2);

        doReturn(messagePage)
                .when(this.messageService).getByChatId(chatId, pageable);
        doReturn(new MessagePayload(1L, chatId, "User 1", "Message 1",
                LocalDateTime.parse("2023-01-01T10:05:00"),
                LocalDateTime.parse("2023-01-02T10:05:00")))
                .when(this.messageMapper).toPayload(
                        Message.builder()
                                .id(1L)
                                .chat(Chat.builder().id(chatId).build())
                                .sender(User.builder().username("User 1").build())
                                .content("Message 1")
                                .createdAt(LocalDateTime.parse("2023-01-01T10:05:00"))
                                .editedAt(LocalDateTime.parse("2023-01-02T10:05:00"))
                                .build());
        doReturn(new MessagePayload(2L, chatId, "User 2", "Message 2",
                LocalDateTime.parse("2023-02-01T10:05:00"),
                LocalDateTime.parse("2023-02-02T10:05:00")))
                .when(this.messageMapper).toPayload(
                        Message.builder()
                                .id(2L)
                                .chat(Chat.builder().id(chatId).build())
                                .sender(User.builder().username("User 2").build())
                                .content("Message 2")
                                .createdAt(LocalDateTime.parse("2023-02-01T10:05:00"))
                                .editedAt(LocalDateTime.parse("2023-02-02T10:05:00"))
                                .build());

        // when
        Page<MessagePayload> result = this.controller.getMessages(chatId, 0, 20, principal);

        // then
        assertEquals(new PageImpl<>(List.of(
                new MessagePayload(
                        1L,
                        chatId,
                        "User 1",
                        "Message 1",
                        LocalDateTime.parse("2023-01-01T10:05:00"),
                        LocalDateTime.parse("2023-01-02T10:05:00")),
                new MessagePayload(
                        2L,
                        chatId,
                        "User 2",
                        "Message 2",
                        LocalDateTime.parse("2023-02-01T10:05:00"),
                        LocalDateTime.parse("2023-02-02T10:05:00"))
        ), pageable, 2), result);

        verify(this.messageService).getByChatId(chatId, pageable);
        verify(messageMapper, times(2)).toPayload(any(Message.class));
        verifyNoMoreInteractions(this.messageService, this.messageMapper);
        verifyNoInteractions(this.notificationService);
    }

    @Test
    void createMessage_ValidRequest_ReturnsCreatedResponse() throws BindException {
        // given
        Long chatId = 1L;
        User sender = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(chatId).build();
        NewMessagePayload payload = new NewMessagePayload("New message");
        Message createdMessage = Message.builder()
                .id(1L)
                .chat(chat)
                .sender(sender)
                .content("New message")
                .createdAt(LocalDateTime.parse("2023-01-01T10:05:00"))
                .build();
        MessagePayload responsePayload = new MessagePayload(1L, chatId, "user1", "New message",
                LocalDateTime.parse("2023-01-01T10:05:00"), null);
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(createdMessage)
                .when(this.messageService).createMessage(chat, sender, payload);
        doReturn(responsePayload)
                .when(this.messageMapper).toPayload(createdMessage);

        // when
        var result = this.controller.createMessage(
                chatId, payload, chat, sender,
                new MapBindingResult(Map.of(), "payload"),
                uriComponentsBuilder
        );

        // then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responsePayload, result.getBody());
        assertTrue(result.getHeaders().getLocation().toString().contains("/api/chats/1/messages/1"));

        verify(this.messageService).createMessage(chat, sender, payload);
        verify(this.messageMapper).toPayload(createdMessage);
        verify(this.notificationService).send(sender, createdMessage, NEW_MESSAGE);
        verifyNoMoreInteractions(this.messageService, this.messageMapper, this.notificationService);
    }

    @Test
    void createMessage_InvalidPayload_ThrowsBindException() {
        // given
        Long chatId = 1L;
        User sender = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(chatId).build();
        NewMessagePayload payload = new NewMessagePayload("");
        MapBindingResult bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "content", "Content is required"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class, () ->
                this.controller.createMessage(
                        chatId, payload, chat, sender,
                        bindingResult, uriComponentsBuilder
                ));

        // then
        assertEquals(List.of(new FieldError("payload", "content", "Content is required")),
                exception.getAllErrors());
        verifyNoInteractions(this.messageService, this.messageMapper, this.notificationService);
    }

    @Test
    void createMessage_InvalidPayloadAndBindingResultIsBindException_ThrowsOriginalException() {
        // given
        Long chatId = 1L;
        User sender = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(chatId).build();
        NewMessagePayload payload = new NewMessagePayload("");
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "content", "Content is required"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class, () ->
                this.controller.createMessage(
                        chatId, payload, chat, sender,
                        bindingResult, uriComponentsBuilder
                ));

        // then
        assertEquals(List.of(new FieldError("payload", "content", "Content is required")),
                exception.getAllErrors());
        verifyNoInteractions(this.messageService, this.messageMapper, this.notificationService);
    }
}