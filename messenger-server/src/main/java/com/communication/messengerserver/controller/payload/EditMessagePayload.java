package com.communication.messengerserver.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditMessagePayload(
        @NotNull(message = "{messenger-server.messages.edit.errors.content_is_null}")
        @NotBlank(message = "{messenger-server.messages.edit.errors.content_is_blank}")
        String content
) {
}
