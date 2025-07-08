package io.github.artemfedorov2004.messengerserver.exception;

public class InvalidChatParticipantsException extends RuntimeException {

    public InvalidChatParticipantsException() {
    }

    public InvalidChatParticipantsException(String message) {
        super(message);
    }

    public InvalidChatParticipantsException(String message, Throwable cause) {
        super(message, cause);
    }
}
