package com.communication.messengerserver.controller.payload;

public record TokensPayload(
        String accessToken,
        String refreshToken
) {
}
