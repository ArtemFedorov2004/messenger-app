package com.communication.messengerserver.entity;

import com.communication.messengerserver.controller.payload.ChatPayload;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChatNotification {
    public enum Type {
        NEW_CHAT,
    }

    private Type type;

    private ChatPayload chat;
}
