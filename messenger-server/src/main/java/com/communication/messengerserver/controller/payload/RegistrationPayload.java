package com.communication.messengerserver.controller.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationPayload(
        @NotNull(message = "{messenger_server.registration.errors.username_is_null}")
        @NotBlank(message = "{messenger_server.registration.errors.username_is_blank}")
        String username,
        @NotNull(message = "{messenger_server.registration.errors.email_is_null}")
        @NotBlank(message = "{messenger_server.registration.errors.email_is_blank}")
        @Email(message = "{messenger_server.registration.errors.email_is_invalid}")
        String email,
        @NotNull(message = "{messenger_server.registration.errors.password_is_null}")
        @NotBlank(message = "{messenger_server.registration.errors.password_is_blank}")
        @Size(min = 8, message = "{messenger_server.registration.errors.password_is_size}")
        String password
) {
}
