package com.communication.messengerserver.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewMessagePayload(
        @NotNull(message = "{messenger-server.messages.create.errors.content_is_null}")
        @NotBlank(message = "{messenger-server.messages.create.errors.content_is_blank}")
        String content,
        @NotNull(message = "{messenger-server.messages.create.errors.senderId_is_null}")
        @NotBlank(message = "{messenger-server.messages.create.errors.senderId_is_blank}")
        String senderId,
        @NotNull(message = "{messenger-server.messages.create.errors.chatId_is_null}")
        @NotBlank(message = "{messenger-server.messages.create.errors.chatId_is_blank}")
        String chatId
) {
}
