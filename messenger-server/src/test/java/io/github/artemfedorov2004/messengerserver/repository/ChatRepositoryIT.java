package io.github.artemfedorov2004.messengerserver.repository;

import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/sql/users.sql")
@Sql("/sql/chats.sql")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatRepositoryIT {

    @Autowired
    ChatRepository chatRepository;

    @Test
    void existsByIdAndParticipantsUsername_UserIsParticipant_ReturnsTrue() {
        // given
        Long chatId = 1L;
        String username = "Artem";

        // when
        boolean result = this.chatRepository.existsByIdAndParticipantsUsername(chatId, username);

        // then
        assertTrue(result);
    }

    @Test
    void existsByIdAndParticipantsUsername_UserIsNotParticipant_ReturnsFalse() {
        // given
        Long chatId = 1L;
        String username = "Pavel";

        // when
        boolean result = this.chatRepository.existsByIdAndParticipantsUsername(chatId, username);

        // then
        assertFalse(result);
    }

    @Test
    void existsByIdAndParticipantsUsername_ChatDoesNotExist_ReturnsFalse() {
        // given
        Long chatId = 100L;
        String username = "Pavel";

        // when
        boolean result = this.chatRepository.existsByIdAndParticipantsUsername(chatId, username);

        // then
        assertFalse(result);
    }

    @Test
    void findAllPrivateChatsByUser_ReturnsPrivateChats() {
        // given
        String username = "Dima";

        // when
        Iterable<Chat> result = this.chatRepository.findAllPrivateChatsByUser(username);

        // then
        List<Chat> chatList = StreamSupport.stream(result.spliterator(), false).toList();
        assertEquals(1, chatList.size());

        Chat chat = chatList.get(0);
        assertEquals(1L, chat.getId());

        Set<String> participantNames = chat.getParticipants()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
        assertEquals(Set.of("Artem", "Dima"), participantNames);
    }

    @Test
    void findOtherParticipants_ReturnsUsers() {
        // given
        Long chatId = 1L;
        String username = "Artem";

        // when
        Set<User> result = this.chatRepository.findOtherParticipants(chatId, username);

        // then
        assertEquals(1, result.size());
        assertEquals("Dima", result.iterator().next().getUsername());
    }

    @Test
    void findOtherParticipants_ChatDoesNotExist_ReturnsEmptySet() {
        // given
        Long chatId = 100L;
        String username = "Artem";

        // when
        Set<User> result = this.chatRepository.findOtherParticipants(chatId, username);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void findPrivateChatByParticipants_ChatExists_ReturnsChat() {
        // given
        String participant1 = "Artem";
        String participant2 = "Dima";

        // when
        Optional<Chat> result = this.chatRepository.findPrivateChatByParticipants(participant1, participant2);

        // then
        assertTrue(result.isPresent());

        Chat chat = result.get();
        assertEquals(1L, chat.getId());

        Set<String> participantNames = chat.getParticipants()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
        assertEquals(Set.of("Artem", "Dima"), participantNames);
    }

    @Test
    void findPrivateChatByParticipants_ChatDoestNotExist_ReturnsEmptyOptional() {
        // given
        String participant1 = "Dima";
        String participant2 = "Pavel";

        // when
        Optional<Chat> result = this.chatRepository.findPrivateChatByParticipants(participant1, participant2);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void findOtherParticipantInPrivateChat_ReturnsUser() {
        // given
        Long chatId = 1L;
        String username = "Artem";

        // when
        Optional<User> participant = this.chatRepository.findOtherParticipantInPrivateChat(chatId, username);

        // then
        assertTrue(participant.isPresent());
        assertEquals("Dima", participant.get().getUsername());
    }

    @Test
    void findOtherParticipantInPrivateChat_UserIsNotParticipant_ReturnsEmptyOptional() {
        // given
        Long chatId = 1L;
        String username = "Pavel";

        // when
        Optional<User> participant = this.chatRepository.findOtherParticipantInPrivateChat(chatId, username);

        // then
        assertTrue(participant.isEmpty());
    }

    @Test
    void findOtherParticipantInPrivateChat_ChatDoesNotExist_ReturnsEmptyOptional() {
        // given
        Long chatId = 100L;
        String username = "Artem";

        // when
        Optional<User> participant = this.chatRepository.findOtherParticipantInPrivateChat(chatId, username);

        // then
        assertTrue(participant.isEmpty());
    }
}