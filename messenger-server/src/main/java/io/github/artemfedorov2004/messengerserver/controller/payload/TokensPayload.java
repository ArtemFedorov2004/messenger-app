package io.github.artemfedorov2004.messengerserver.controller.payload;

public record TokensPayload(
        String accessToken,
        String refreshToken
) {
}
