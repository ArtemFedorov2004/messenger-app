package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.exception.AlreadyExistsException;
import io.github.artemfedorov2004.messengerserver.exception.InvalidChatParticipantsException;
import io.github.artemfedorov2004.messengerserver.exception.MissingRefreshTokenCookieException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {

    private final MessageSource messageSource;

    private final GlobalExceptionHandler globalExceptionHandler;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        this.messageSource.getMessage("errors.400.title", new Object[0],
                                "errors.400.title", locale));
        problemDetail.setProperty("errors",
                exception.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .toList());

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyExistsException(AlreadyExistsException exception,
                                                                      Locale locale) {
        return this.globalExceptionHandler.handleException(exception, HttpStatus.BAD_REQUEST, locale);
    }

    @ExceptionHandler(MissingRefreshTokenCookieException.class)
    public ResponseEntity<ProblemDetail> handleMissingRefreshTokenCookieException(MissingRefreshTokenCookieException exception,
                                                                                  Locale locale) {
        return this.globalExceptionHandler.handleException(exception, HttpStatus.BAD_REQUEST, locale);
    }

    @ExceptionHandler(InvalidChatParticipantsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidChatParticipantsException(InvalidChatParticipantsException exception,
                                                                                Locale locale) {
        return this.globalExceptionHandler.handleException(exception, HttpStatus.BAD_REQUEST, locale);
    }
}
