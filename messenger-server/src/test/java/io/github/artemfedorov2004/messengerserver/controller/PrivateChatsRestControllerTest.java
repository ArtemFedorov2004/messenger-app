package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.NewPrivateChatPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.PrivateChatPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.ChatMapper;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.AlreadyExistsException;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import io.github.artemfedorov2004.messengerserver.service.ChatService;
import io.github.artemfedorov2004.messengerserver.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.artemfedorov2004.messengerserver.entity.PrivateChatNotification.Type.NEW_CHAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrivateChatsRestControllerTest {

    @Mock
    ChatService chatService;

    @Mock
    ChatMapper chatMapper;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    PrivateChatsRestController controller;

    @Test
    void getPrivateChats_ReturnsListOfChats() {
        // given
        User principal = User.builder().username("user1").build();
        Set<User> participants = Set.of(
                User.builder().username("user1").build(),
                User.builder().username("user2").build());
        List<Chat> chats = List.of(
                Chat.builder()
                        .id(1L)
                        .participants(participants)
                        .build());

        doReturn(chats)
                .when(this.chatService).getPrivateChats(principal);
        doReturn(List.of(
                new PrivateChatPayload(1L, "user2", null),
                new PrivateChatPayload(2L, "user3", null)))
                .when(this.chatMapper).toPayload(chats, principal);

        // when
        Iterable<PrivateChatPayload> result = this.controller.getPrivateChats(principal);

        // then
        assertEquals(List.of(
                new PrivateChatPayload(1L, "user2", null),
                new PrivateChatPayload(2L, "user3", null)), result);

        verify(this.chatService).getPrivateChats(principal);
        verify(this.chatMapper).toPayload(chats, principal);
        verifyNoMoreInteractions(this.chatService, this.chatMapper);
        verifyNoInteractions(this.notificationService);
    }

    @Test
    void createPrivateChat_ValidRequest_ReturnsCreatedResponse() throws BindException {
        // given
        User principal = User.builder().username("user1").build();
        NewPrivateChatPayload payload = new NewPrivateChatPayload("user2");
        Set<User> participants = Set.of(
                User.builder().username("user1").build(),
                User.builder().username("user2").build());
        Chat createdChat = Chat.builder()
                .id(1L)
                .participants(participants)
                .build();
        PrivateChatPayload responsePayload = new PrivateChatPayload(1L, "user2", null);
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(createdChat)
                .when(this.chatService).createPrivateChat("user1", "user2");
        doReturn(responsePayload)
                .when(this.chatMapper).toPayload(createdChat, principal);

        // when
        var result = this.controller.createPrivateChat(
                payload, principal,
                new MapBindingResult(Map.of(), "payload"),
                uriComponentsBuilder
        );

        // then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responsePayload, result.getBody());
        assertTrue(result.getHeaders().getLocation().toString().contains("/api/chats/1"));

        verify(this.chatService).createPrivateChat("user1", "user2");
        verify(this.chatMapper).toPayload(createdChat, principal);
        verify(this.notificationService).send(principal, createdChat, NEW_CHAT);
        verifyNoMoreInteractions(this.chatService, this.chatMapper, this.notificationService);
    }

    @Test
    void createPrivateChat_InvalidPayload_ThrowsBindException() {
        // given
        User principal = User.builder().username("user1").build();
        NewPrivateChatPayload payload = new NewPrivateChatPayload("");
        MapBindingResult bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "participantName", "Participant name is required"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class, () ->
                this.controller.createPrivateChat(
                        payload, principal,
                        bindingResult, uriComponentsBuilder
                ));

        // then
        assertEquals(List.of(new FieldError("payload", "participantName", "Participant name is required")), exception.getAllErrors());
        verifyNoInteractions(this.chatService, this.chatMapper, this.notificationService);
    }

    @Test
    void createPrivateChat_InvalidPayloadAndBindingResultIsBindException_ThrowsOriginalException() {
        // given
        User principal = User.builder().username("user1").build();
        NewPrivateChatPayload payload = new NewPrivateChatPayload("");
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "participantName", "Participant name is required"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class, () ->
                this.controller.createPrivateChat(
                        payload, principal,
                        bindingResult, uriComponentsBuilder
                ));

        // then
        assertEquals(List.of(new FieldError("payload", "participantName", "Participant name is required")), exception.getAllErrors());
        verifyNoInteractions(this.chatService, this.chatMapper, this.notificationService);
    }

    @Test
    void createPrivateChat_WhenParticipantNotFound_ThrowsResourceNotFoundException() {
        // given
        User principal = User.builder().username("user1").build();
        NewPrivateChatPayload payload = new NewPrivateChatPayload("nonexistent");
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doThrow(new ResourceNotFoundException("messenger_server.chats.create.errors.participants_not_found"))
                .when(this.chatService).createPrivateChat("user1", "nonexistent");

        // when
        var exception = assertThrows(ResourceNotFoundException.class, () ->
                this.controller.createPrivateChat(
                        payload, principal,
                        new MapBindingResult(Map.of(), "payload"),
                        uriComponentsBuilder
                ));

        // then
        assertEquals("messenger_server.chats.create.errors.participants_not_found", exception.getMessage());

        verify(this.chatService).createPrivateChat("user1", "nonexistent");
        verifyNoMoreInteractions(this.chatService);
        verifyNoInteractions(this.chatMapper, this.notificationService);
    }

    @Test
    void createPrivateChat_WhenChatAlreadyExists_ThrowsAlreadyExistsException() {
        // given
        User principal = User.builder().username("user1").build();
        NewPrivateChatPayload payload = new NewPrivateChatPayload("user2");
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doThrow(new AlreadyExistsException("messenger_server.chats.create.errors.private_chat_already_exists"))
                .when(this.chatService).createPrivateChat("user1", "user2");

        // when
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () ->
                this.controller.createPrivateChat(
                        payload, principal,
                        new MapBindingResult(Map.of(), "payload"),
                        uriComponentsBuilder
                ));

        // then
        assertEquals("messenger_server.chats.create.errors.private_chat_already_exists", exception.getMessage());

        verify(this.chatService).createPrivateChat("user1", "user2");
        verifyNoMoreInteractions(this.chatService);
        verifyNoInteractions(this.chatMapper, this.notificationService);
    }
}