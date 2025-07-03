package io.github.artemfedorov2004.messengerserver.entity;

import io.github.artemfedorov2004.messengerserver.controller.payload.PrivateChatPayload;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PrivateChatNotification {
    public enum Type {
        NEW_CHAT,
        EDIT_CHAT,
    }

    private Type type;

    private PrivateChatPayload chat;
}
