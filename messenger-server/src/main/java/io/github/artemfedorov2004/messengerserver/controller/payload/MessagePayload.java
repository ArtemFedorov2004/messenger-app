package io.github.artemfedorov2004.messengerserver.controller.payload;

import java.time.LocalDateTime;

public record MessagePayload(
        Long id,
        Long chatId,
        String senderName,
        String content,
        LocalDateTime createdAt,
        LocalDateTime editedAt
) {
}
