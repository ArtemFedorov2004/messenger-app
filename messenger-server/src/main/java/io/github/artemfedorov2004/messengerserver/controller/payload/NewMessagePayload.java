package io.github.artemfedorov2004.messengerserver.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewMessagePayload(
        @NotNull(message = "{messenger_server.messages.create.errors.content_is_null}")
        @NotBlank(message = "{messenger_server.messages.create.errors.content_is_blank}")
        String content
) {
}
