package com.communication.messengerserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.NoSuchElementException;

@ControllerAdvice
@RequiredArgsConstructor
public class NotFoundControllerAdvice {

    private final GlobalExceptionHandler globalExceptionHandler;

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException exception,
                                                                      Locale locale) {
        return this.globalExceptionHandler.handleException(exception, HttpStatus.NOT_FOUND, locale);
    }
}
