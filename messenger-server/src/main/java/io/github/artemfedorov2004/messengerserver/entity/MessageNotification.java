package io.github.artemfedorov2004.messengerserver.entity;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
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

    private MessagePayload message;
}
