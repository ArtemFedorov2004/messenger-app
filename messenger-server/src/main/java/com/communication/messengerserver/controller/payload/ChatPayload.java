package com.communication.messengerserver.controller.payload;

import com.communication.messengerserver.entity.Message;

import java.util.List;

public record ChatPayload(
        String id,
        String title,
        List<Message> messages
) {
}
