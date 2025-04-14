package com.communication.messengerserver.controller.payload;

public record UserPayload(
        String id,
        String username,
        String email
) {
}
