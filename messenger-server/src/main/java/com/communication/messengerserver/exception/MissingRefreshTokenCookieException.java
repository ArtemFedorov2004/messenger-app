package com.communication.messengerserver.exception;

public class MissingRefreshTokenCookieException extends RuntimeException {

    public MissingRefreshTokenCookieException() {
    }

    public MissingRefreshTokenCookieException(String message) {
        super(message);
    }

    public MissingRefreshTokenCookieException(String message, Throwable cause) {
        super(message, cause);
    }
}
