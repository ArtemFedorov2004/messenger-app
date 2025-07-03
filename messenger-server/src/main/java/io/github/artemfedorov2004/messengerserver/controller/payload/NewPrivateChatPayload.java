package io.github.artemfedorov2004.messengerserver.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewPrivateChatPayload(
        @NotNull(message = "{messenger_server.chats.create.errors.participant_name_is_null}")
        @NotBlank(message = "{messenger_server.chats.create.errors.participant_name_is_blank}")
        String participantName
) {
}
