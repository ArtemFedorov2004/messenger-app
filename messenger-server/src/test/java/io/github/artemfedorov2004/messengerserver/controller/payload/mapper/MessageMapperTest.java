package io.github.artemfedorov2004.messengerserver.controller.payload.mapper;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MessageMapperTest {

    @InjectMocks
    MessageMapper messageMapper;

    @Test
    void toPayload_ConvertsMessageToPayload() {
        // given
        LocalDateTime createdAt = LocalDateTime.parse("2023-01-01T10:00:00");
        LocalDateTime editedAt = LocalDateTime.parse("2023-01-01T10:05:00");

        Message message = Message.builder()
                .id(1L)
                .chat(new Chat(100L, Set.of()))
                .sender(User.builder().username("user1").build())
                .content("Test message")
                .createdAt(createdAt)
                .editedAt(editedAt)
                .build();

        // when
        MessagePayload payload = this.messageMapper.toPayload(message);

        // then
        assertEquals(new MessagePayload(1L, 100L, "user1",
                "Test message", createdAt, editedAt), payload);
    }

    @Test
    void fromPayload_ConvertsPayloadToMessage() {
        // given
        LocalDateTime createdAt = LocalDateTime.parse("2023-01-01T10:00:00");
        LocalDateTime editedAt = LocalDateTime.parse("2023-01-01T10:05:00");

        MessagePayload payload = new MessagePayload(
                1L,
                100L,
                "user1",
                "Test message",
                createdAt,
                editedAt
        );

        // when
        Message message = this.messageMapper.fromPayload(payload);

        // then
        assertEquals(new Message(
                1L,
                Chat.builder()
                        .id(100L)
                        .participants(Set.of())
                        .build(),
                User.builder().username("user1").build(),
                "Test message", createdAt, editedAt), message);
    }

    @Test
    void toPayload_ForIterable_ConvertsAllMessages() {
        // given
        List<Message> messages = List.of(
                Message.builder()
                        .id(1L)
                        .chat(new Chat(100L, Set.of()))
                        .sender(User.builder().username("user1").build())
                        .content("Message 1")
                        .createdAt(LocalDateTime.parse("2023-01-01T10:00:00"))
                        .build(),
                Message.builder()
                        .id(2L)
                        .chat(new Chat(100L, Set.of()))
                        .sender(User.builder().username("user2").build())
                        .content("Message 2")
                        .createdAt(LocalDateTime.parse("2023-02-01T10:00:00"))
                        .build()
        );

        // when
        Iterable<MessagePayload> payloads = this.messageMapper.toPayload(messages);

        // then
        assertEquals(List.of(
                new MessagePayload(1L, 100L, "user1", "Message 1", LocalDateTime.parse("2023-01-01T10:00:00"), null),
                new MessagePayload(2L, 100L, "user2", "Message 2", LocalDateTime.parse("2023-02-01T10:00:00"), null)
        ), payloads);
    }
}