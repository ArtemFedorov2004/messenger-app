package com.communication.messengerserver.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HelloControllerTest {

    HelloController helloController = new HelloController();

    @Test
    void simpleTest() {

        var result = this.helloController.hello();
        assertEquals("Hello world", result.getBody());
    }
}