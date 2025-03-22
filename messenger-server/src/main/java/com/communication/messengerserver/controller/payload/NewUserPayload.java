package com.communication.messengerserver.controller.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewUserPayload(
        @NotNull(message = "{messenger-server.users.errors.username_is_null}")
        @NotBlank(message = "{messenger-server.users.errors.username_is_blank}")
        String username,
        @NotNull(message = "{messenger-server.users.errors.firstname_is_null}")
        @NotBlank(message = "{messenger-server.users.errors.firstname_is_blank}")
        String firstname,
        @NotNull(message = "{messenger-server.users.errors.lastname_is_null}")
        @NotBlank(message = "{messenger-server.users.errors.lastname_is_blank}")
        String lastname,
        @NotNull(message = "{messenger-server.users.errors.email_is_null}")
        @NotBlank(message = "{messenger-server.users.errors.email_is_blank}")
        @Email(message = "{messenger-server.users.errors.email_is_invalid}")
        String email
) {
}
