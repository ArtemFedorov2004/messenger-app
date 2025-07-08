package io.github.artemfedorov2004.messengerserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public ResponseEntity<ProblemDetail> handleException(Exception exception, HttpStatus status, Locale locale) {
        return ResponseEntity.status(status)
                .body(ProblemDetail.forStatusAndDetail(
                        status,
                        this.messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale)
                ));
    }
}
