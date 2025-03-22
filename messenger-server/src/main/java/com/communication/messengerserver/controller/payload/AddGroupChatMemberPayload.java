package com.communication.messengerserver.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddGroupChatMemberPayload(
        @NotNull(message = "{messenger-server.group_chat_members.add.errors.id_is_null}")
        @NotBlank(message = "{messenger-server.group_chat_members.add.errors.id_is_blank}")
        String userId
) {
}
