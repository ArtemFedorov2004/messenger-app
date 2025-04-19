package com.communication.messengerserver.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginPayload(
        @NotNull(message = "{messenger_server.login.errors.username_is_null}")
        @NotBlank(message = "{messenger_server.login.errors.username_is_blank}")
        String username,
        @NotNull(message = "{messenger_server.login.errors.password_is_null}")
        @NotBlank(message = "{messenger_server.login.errors.password_is_blank}")
        @Size(min = 8, message = "{messenger_server.login.errors.password_is_size}")
        String password
) {
}
