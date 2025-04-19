package com.communication.messengerserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MessageNotification {
    public enum Type {
        NEW_MESSAGE,
        EDIT_MESSAGE,
        DELETE_MESSAGE
    }

    private Type type;

    private Message message;
}
