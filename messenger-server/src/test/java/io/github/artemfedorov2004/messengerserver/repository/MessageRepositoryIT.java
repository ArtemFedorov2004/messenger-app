package io.github.artemfedorov2004.messengerserver.repository;

import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql({
        "/sql/users.sql",
        "/sql/chats.sql",
        "/sql/messages.sql"
})
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryIT {

    @Autowired
    MessageRepository messageRepository;

    @Test
    void findByChatId_ReturnsPaginatedMessages() {
        // given
        Long chatId = 1L;
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<Message> result = this.messageRepository.findByChatId(chatId, pageable);

        // then
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());

        Message message = result.getContent().getFirst();
        assertEquals(2L, message.getId());
        assertEquals("Message 2", message.getContent());
        assertEquals(LocalDateTime.parse("2023-01-01T11:00:00"), message.getCreatedAt());
        assertEquals(LocalDateTime.parse("2023-01-01T11:30:00"), message.getEditedAt());
    }

    @Test
    void existsByIdAndSender_MessageBelongsToUser_ReturnsTrue() {
        // given
        User user = User.builder()
                .username("Artem")
                .build();
        Long messageId = 1L;

        // when
        boolean result = this.messageRepository.existsByIdAndSender(messageId, user);

        // then
        assertTrue(result);
    }

    @Test
    void existsByIdAndSender_MessageDoesNotBelongToUser_ReturnsFalse() {
        // given
        User user = User.builder()
                .username("Dima")
                .build();
        Long messageId = 1L;

        // when
        boolean result = this.messageRepository.existsByIdAndSender(messageId, user);

        // then
        assertFalse(result);
    }

    @Test
    void existsByIdAndChatParticipantsContaining_UserIsParticipant_ReturnsTrue() {
        // given
        User user = User.builder()
                .username("Artem")
                .build();
        Long messageId = 1L;

        // when
        boolean result = this.messageRepository.existsByIdAndChatParticipantsContaining(messageId, user);

        // then
        assertTrue(result);
    }

    @Test
    void existsByIdAndChatParticipantsContaining_UserNotParticipant_ReturnsFalse() {
        // given
        User user = User.builder()
                .username("Pavel")
                .build();
        Long messageId = 1L;

        // when
        boolean result = this.messageRepository.existsByIdAndChatParticipantsContaining(messageId, user);

        // then
        assertFalse(result);
    }

    @Test
    void findLastMessageByChatId_ReturnsMostRecentMessage() {
        // given
        Long chatId = 1L;

        // when
        Optional<Message> result = this.messageRepository.findLastMessageByChatId(chatId);

        // then
        assertTrue(result.isPresent());

        Message message = result.get();
        assertEquals(2L, message.getId());
        assertEquals("Message 2", message.getContent());
        assertEquals(LocalDateTime.parse("2023-01-01T11:00:00"), message.getCreatedAt());
        assertEquals(LocalDateTime.parse("2023-01-01T11:30:00"), message.getEditedAt());
    }

    @Test
    void findLastMessageByChatId_NoMessages_ReturnsEmptyOptional() {
        // given
        Long chatId = 2L;

        // when
        Optional<Message> result = this.messageRepository.findLastMessageByChatId(chatId);

        // then
        assertTrue(result.isEmpty());
    }
}