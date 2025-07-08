package io.github.artemfedorov2004.messengerserver.controller.payload;

public record PrivateChatPayload(
        Long id,
        String participantName,
        MessagePayload lastMessage
) {
}
