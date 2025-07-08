package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.AlreadyExistsException;
import io.github.artemfedorov2004.messengerserver.exception.InvalidChatParticipantsException;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import io.github.artemfedorov2004.messengerserver.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultChatServiceTest {

    @Mock
    ChatRepository chatRepository;

    @Mock
    UserService userService;

    @InjectMocks
    DefaultChatService service;

    @Test
    void createPrivateChat_ReturnsCreatedChat() {
        // given
        Set<User> participants = Set.of(
                new User("user1", null, null, null),
                new User("user2", null, null, null)
        );

        doReturn(true)
                .when(this.userService).existsAllByUsernames(Set.of("user1", "user2"));
        doReturn(Optional.empty())
                .when(this.chatRepository).findPrivateChatByParticipants("user1", "user2");
        doReturn(new Chat(1L, participants))
                .when(this.chatRepository).save(new Chat(null, participants));

        // when
        Chat result = this.service.createPrivateChat("user1", "user2");

        // then
        assertEquals(new Chat(1L, participants), result);
        verify(this.userService).existsAllByUsernames(Set.of("user1", "user2"));
        verify(this.chatRepository).findPrivateChatByParticipants("user1", "user2");
        verify(this.chatRepository).save(new Chat(null, participants));
        verifyNoMoreInteractions(this.userService, this.chatRepository);
    }

    @Test
    void createPrivateChat_WithSelf_ThrowsInvalidChatParticipantsException() {
        // given
        String username = "Artem";

        // when
        var exception = assertThrows(InvalidChatParticipantsException.class,
                () -> this.service.createPrivateChat(username, username));

        // then
        assertEquals("messenger_server.chats.create.errors.cannot_create_with_yourself",
                exception.getMessage());

        verifyNoInteractions(this.userService, this.chatRepository);
    }

    @Test
    void createPrivateChat_ParticipantNotFound_ThrowsInvalidChatParticipantsException() {
        // given
        doReturn(false)
                .when(this.userService).existsAllByUsernames(Set.of("user1", "unknown"));

        // when
        var exception = assertThrows(InvalidChatParticipantsException.class, () ->
                this.service.createPrivateChat("user1", "unknown"));

        // then
        assertEquals("messenger_server.chats.create.errors.participants_not_found",
                exception.getMessage());

        verify(this.userService).existsAllByUsernames(Set.of("user1", "unknown"));
        verifyNoMoreInteractions(this.userService);
        verifyNoInteractions(this.chatRepository);
    }

    @Test
    void createPrivateChat_PrivateChatAlreadyExists_ThrowsAlreadyExistsException() {
        // given
        Set<User> participants = Set.of(
                new User("user1", null, null, Role.ROLE_USER),
                new User("user2", null, null, Role.ROLE_USER)
        );

        doReturn(true)
                .when(this.userService).existsAllByUsernames(Set.of("user1", "user2"));
        doReturn(Optional.of(
                Chat.builder()
                        .id(1L)
                        .participants(participants)
                        .build()))
                .when(this.chatRepository).findPrivateChatByParticipants("user1", "user2");

        // when
        var exception = assertThrows(AlreadyExistsException.class, () ->
                this.service.createPrivateChat("user1", "user2"));

        // then
        assertEquals("messenger_server.chats.create.errors.private_chat_already_exists",
                exception.getMessage());

        verify(this.userService).existsAllByUsernames(Set.of("user1", "user2"));
        verify(this.chatRepository).findPrivateChatByParticipants("user1", "user2");
        verifyNoMoreInteractions(this.userService, this.chatRepository);
    }

    @Test
    void getChat_ChatExists_ReturnsChat() {
        // given
        Long chatId = 1L;

        doReturn(Optional.of(new Chat(chatId, Collections.emptySet())))
                .when(this.chatRepository).findById(chatId);

        // when
        Chat result = this.service.getChat(chatId);

        // then
        assertEquals(new Chat(chatId, Collections.emptySet()), result);
        verify(this.chatRepository).findById(chatId);
        verifyNoMoreInteractions(this.chatRepository);
        verifyNoInteractions(this.userService);
    }

    @Test
    void getChat_ChatNotExists_ThrowsResourceNotFoundException() {
        // given
        Long chatId = 1L;

        doReturn(Optional.empty())
                .when(this.chatRepository).findById(chatId);

        // when
        var exception = assertThrows(ResourceNotFoundException.class, () ->
                this.service.getChat(chatId));

        // then
        assertEquals("messenger_server.errors.chat_not_found",
                exception.getMessage());
        verify(this.chatRepository).findById(chatId);
        verifyNoMoreInteractions(this.chatRepository);
        verifyNoInteractions(this.userService);
    }

    @Test
    void isParticipant_UserIsParticipant_ReturnsTrue() {
        // given
        Long chatId = 1L;
        String username = "user1";

        doReturn(true)
                .when(this.chatRepository).existsByIdAndParticipantsUsername(chatId, username);

        // when
        boolean result = this.service.isParticipant(chatId, User.builder()
                .username(username)
                .build());

        // then
        assertTrue(result);
        verify(this.chatRepository).existsByIdAndParticipantsUsername(chatId, username);
        verifyNoMoreInteractions(this.chatRepository);
        verifyNoInteractions(this.userService);
    }

    @Test
    void isParticipant_UserIsNotParticipant_ReturnsFalse() {
        // given
        Long chatId = 1L;
        String username = "user1";

        doReturn(false)
                .when(this.chatRepository).existsByIdAndParticipantsUsername(chatId, username);

        // when
        boolean result = this.service.isParticipant(chatId, User.builder()
                .username(username)
                .build());

        // then
        assertFalse(result);
        verify(this.chatRepository).existsByIdAndParticipantsUsername(chatId, username);
        verifyNoMoreInteractions(this.chatRepository);
        verifyNoInteractions(this.userService);
    }

    @Test
    void getPrivateChats_ReturnsChatsForUser() {
        // given
        User user = new User("user1", "email", "pass", Role.ROLE_USER);

        doReturn(Collections.singletonList(new Chat(1L, Set.of(user))))
                .when(this.chatRepository).findAllPrivateChatsByUser("user1");

        // when
        Iterable<Chat> result = this.service.getPrivateChats(user);

        // then
        assertEquals(Collections.singletonList(new Chat(1L, Set.of(user))), result);
        verify(this.chatRepository).findAllPrivateChatsByUser("user1");
        verifyNoMoreInteractions(this.chatRepository);
        verifyNoInteractions(this.userService);
    }


    @Test
    void getOtherParticipants_UserIsParticipant_ReturnsOtherParticipants() {
        // given
        User currentUser = User.builder().username("user1").build();
        Set<User> expectedParticipants = Set.of(
                User.builder().username("user2").build(),
                User.builder().username("user3").build()
        );

        doReturn(true)
                .when(this.chatRepository).existsByIdAndParticipantsUsername(1L, "user1");
        doReturn(expectedParticipants)
                .when(this.chatRepository).findOtherParticipants(1L, "user1");

        // when
        Set<User> result = this.service.getOtherParticipants(1L, currentUser);

        // then
        assertEquals(expectedParticipants, result);
        verify(this.chatRepository).existsByIdAndParticipantsUsername(1L, "user1");
        verify(this.chatRepository).findOtherParticipants(1L, "user1");
        verifyNoMoreInteractions(this.chatRepository, this.userService);
    }

    @Test
    void getOtherParticipants_UserNotParticipant_ThrowsRuntimeException() {
        // given
        Long chatId = 1L;
        User currentUser = User.builder().username("user1").build();

        doReturn(false)
                .when(this.chatRepository).existsByIdAndParticipantsUsername(1L, "user1");

        // when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> this.service.getOtherParticipants(chatId, currentUser));

        // then
        assertEquals("User user1 is not a participant of chat 1", exception.getMessage());
        verify(this.chatRepository).existsByIdAndParticipantsUsername(1L, "user1");
        verifyNoMoreInteractions(this.userService, this.chatRepository);
    }

    @Test
    void getOtherParticipantInPrivateChat_ChatExists_ReturnsOtherParticipant() {
        // given
        Long chatId = 1L;
        User currentUser = User.builder().username("user1").build();
        User otherUser = User.builder().username("user2").build();

        doReturn(true)
                .when(this.chatRepository).existsById(chatId);
        doReturn(Optional.of(otherUser))
                .when(this.chatRepository).findOtherParticipantInPrivateChat(chatId, currentUser.getUsername());

        // when
        User result = this.service.getOtherParticipantInPrivateChat(chatId, currentUser);

        // then
        assertEquals(otherUser, result);
        verify(this.chatRepository).existsById(chatId);
        verify(this.chatRepository).findOtherParticipantInPrivateChat(chatId, currentUser.getUsername());
        verifyNoMoreInteractions(this.chatRepository);
        verifyNoInteractions(this.userService);
    }

    @Test
    void getOtherParticipantInPrivateChat_ChatDoesNotExist_ThrowsResourceNotFoundException() {
        // given
        Long chatId = 1L;
        User currentUser = User.builder().username("user1").build();

        doReturn(false)
                .when(this.chatRepository).existsById(chatId);

        // when
        var exception = assertThrows(ResourceNotFoundException.class,
                () -> this.service.getOtherParticipantInPrivateChat(chatId, currentUser));

        // then
        assertEquals("messenger_server.errors.chat_not_found", exception.getMessage());

        verify(this.chatRepository).existsById(chatId);
        verifyNoMoreInteractions(this.chatRepository);
        verifyNoInteractions(this.userService);
    }

    @Test
    void getOtherParticipantInPrivateChat_UserIsNotParticipantOfChat_ThrowsRuntimeException() {
        // given
        Long chatId = 1L;
        User currentUser = User.builder().username("user1").build();
        Chat chat = Chat.builder().id(chatId).build();

        doReturn(true)
                .when(this.chatRepository).existsById(chatId);
        doReturn(Optional.empty())
                .when(this.chatRepository).findOtherParticipantInPrivateChat(chatId, currentUser.getUsername());

        // when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> this.service.getOtherParticipantInPrivateChat(chatId, currentUser));

        // then
        assertEquals("User user1 is not a participant of chat 1", exception.getMessage());

        verify(this.chatRepository).existsById(chatId);
        verify(this.chatRepository).findOtherParticipantInPrivateChat(chatId, currentUser.getUsername());
        verifyNoMoreInteractions(this.chatRepository);
        verifyNoInteractions(this.userService);
    }

    @Test
    void existsById_ChatExists_ReturnsTrue() {
        // given
        Long chatId = 1L;

        doReturn(true)
                .when(this.chatRepository).existsById(chatId);

        // when
        boolean result = this.service.existsById(chatId);

        // then
        assertTrue(result);
        verify(this.chatRepository).existsById(chatId);
        verifyNoMoreInteractions(this.chatRepository);
    }

    @Test
    void existsById_ChatNotExists_ReturnsFalse() {
        // given
        Long chatId = 1L;

        doReturn(false)
                .when(this.chatRepository).existsById(chatId);

        // when
        boolean result = this.service.existsById(chatId);

        // then
        assertFalse(result);
        verify(this.chatRepository).existsById(chatId);
        verifyNoMoreInteractions(this.chatRepository);
    }
}