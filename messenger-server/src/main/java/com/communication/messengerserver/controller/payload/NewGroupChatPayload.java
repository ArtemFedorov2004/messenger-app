package com.communication.messengerserver.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NewGroupChatPayload(
        @NotNull(message = "{messenger-server.group_chats.create.errors.title_is_null}")
        @NotBlank(message = "{messenger-server.group_chats.create.errors.title_is_blank}")
        String title,
        @NotNull(message = "{messenger-server.group_chats.create.errors.members_list_is_empty}")
        List<String> memberIds) {
}
