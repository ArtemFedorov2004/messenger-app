package io.github.artemfedorov2004.messengerserver.controller.payload.mapper;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.PrivateChatPayload;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import io.github.artemfedorov2004.messengerserver.service.ChatService;
import io.github.artemfedorov2004.messengerserver.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMapperTest {

    @Mock
    ChatService chatService;

    @Mock
    MessageService messageService;

    @Mock
    MessageMapper messageMapper;

    @InjectMocks
    ChatMapper chatMapper;

    @Test
    void toPayload_UserIsNotParticipantOfChat_ThrowsResourceNotFoundException() {
        // given
        Long chatId = 1L;
        User principal = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(chatId).build();

        doThrow(new ResourceNotFoundException("messenger_server.errors.chat.participant_not_found"))
                .when(this.chatService).getOtherParticipantInPrivateChat(chatId, principal);

        // when
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> this.chatMapper.toPayload(chat, principal));

        // then
        assertEquals("messenger_server.errors.chat.participant_not_found", exception.getMessage());

        verify(this.chatService).getOtherParticipantInPrivateChat(chatId, principal);
        verifyNoInteractions(this.messageService, this.messageMapper);
        verifyNoMoreInteractions(this.chatService);
    }


    @Test
    void toPayload_LastMessageExist_ReturnsPayload() {
        // given
        Long chatId = 1L;
        User principal = User.builder().username("user1").build();
        User otherParticipant = User.builder().username("user2").build();
        Chat chat = Chat.builder().id(chatId).build();
        LocalDateTime createdAt = LocalDateTime.now();

        Message lastMessage = Message.builder()
                .id(1L)
                .content("Test message")
                .createdAt(LocalDateTime.parse("2015-08-04T10:11:30"))
                .editedAt(LocalDateTime.parse("2025-08-04T10:11:30"))
                .build();

        MessagePayload messagePayload = new MessagePayload(
                1L,
                1L,
                "user1",
                "Test message",
                LocalDateTime.parse("2015-08-04T10:11:30"),
                LocalDateTime.parse("2025-08-04T10:11:30"));

        doReturn(otherParticipant)
                .when(this.chatService).getOtherParticipantInPrivateChat(chatId, principal);
        doReturn(Optional.of(lastMessage))
                .when(this.messageService).findLastMessageByChatId(chatId);
        doReturn(messagePayload)
                .when(this.messageMapper).toPayload(lastMessage);

        // when
        PrivateChatPayload result = this.chatMapper.toPayload(chat, principal);

        // then
        assertEquals(new PrivateChatPayload(1L, "user2", messagePayload), result);

        verify(this.chatService).getOtherParticipantInPrivateChat(chatId, principal);
        verify(this.messageService).findLastMessageByChatId(chatId);
        verify(this.messageMapper).toPayload(lastMessage);
        verifyNoMoreInteractions(this.chatService, this.messageService, this.messageMapper);
    }

    @Test
    void toPayload_LastMessageDoesNotExist_ReturnsPayloadWithNullMessage() {
        // given
        Long chatId = 1L;
        User principal = User.builder().username("user1").build();
        User otherParticipant = User.builder().username("user2").build();
        Chat chat = Chat.builder().id(chatId).build();

        doReturn(otherParticipant)
                .when(this.chatService).getOtherParticipantInPrivateChat(chatId, principal);
        doReturn(Optional.empty())
                .when(this.messageService).findLastMessageByChatId(chatId);

        // when
        PrivateChatPayload result = this.chatMapper.toPayload(chat, principal);

        // then
        assertEquals(new PrivateChatPayload(1L, "user2", null), result);

        verify(this.chatService).getOtherParticipantInPrivateChat(chatId, principal);
        verify(this.messageService).findLastMessageByChatId(chatId);
        verifyNoInteractions(this.messageMapper);
        verifyNoMoreInteractions(this.chatService, this.messageService);
    }

    @Test
    void toPayload_ForIterable_ReturnsListOfPayloads() {
        // given
        User principal = User.builder().username("user1").build();
        List<Chat> chats = List.of(
                Chat.builder().id(1L).build(),
                Chat.builder().id(2L).build()
        );

        User participant1 = User.builder().username("user2").build();
        User participant2 = User.builder().username("user3").build();

        doReturn(participant1)
                .when(this.chatService).getOtherParticipantInPrivateChat(1L, principal);
        doReturn(participant2)
                .when(this.chatService).getOtherParticipantInPrivateChat(2L, principal);
        doReturn(Optional.empty())
                .when(this.messageService).findLastMessageByChatId(anyLong());

        // when
        Iterable<PrivateChatPayload> results = this.chatMapper.toPayload(chats, principal);

        // then
        assertEquals(List.of(
                new PrivateChatPayload(1L, "user2", null),
                new PrivateChatPayload(2L, "user3", null)
        ), results);

        verify(this.chatService, times(2)).getOtherParticipantInPrivateChat(anyLong(), eq(principal));
        verify(this.messageService, times(2)).findLastMessageByChatId(anyLong());
        verifyNoInteractions(this.messageMapper);
        verifyNoMoreInteractions(this.chatService, this.messageService);
    }
}