package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {
        "/sql/users.sql",
        "/sql/chats.sql",
        "/sql/messages.sql"
})
class MessagesRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    RequestPostProcessor authentication;

    @BeforeEach
    void setUp() {
        authentication = user(new User(
                "Artem",
                "artem@workmail.com",
                "$2a$10$gX1CW8m2TqS/ckSkoUC12ueKPfWBwYC9HtAg9prF4bxeAaZoO46me",
                Role.ROLE_USER
        ));
    }

    @Test
    void getMessages_ReturnsPaginatedMessages() throws Exception {
        // given
        Long chatId = 1L;
        var requestBuilder = MockMvcRequestBuilders.get("/api/private-chats/{chatId}/messages", chatId)
                .param("page", "0")
                .param("size", "1")
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "content": [
                                        {
                                            "id":2,
                                            "chatId":1,
                                            "senderName":"Dima",
                                            "content":"Message 2",
                                            "createdAt":"2023-01-01T11:00:00",
                                            "editedAt":"2023-01-01T11:30:00"
                                        }
                                    ],
                                    "totalPages":2
                                }""")
                );
    }

    @Test
    void getMessages_ChatNotFound_ReturnsNotFound() throws Exception {
        // given
        Long chatId = 999L;
        var requestBuilder = MockMvcRequestBuilders.get("/api/private-chats/{chatId}/messages", chatId)
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void getMessages_UserNotAuthenticated_ReturnsForbidden() throws Exception {
        // given
        Long chatId = 1L;
        var requestBuilder = MockMvcRequestBuilders.get("/api/private-chats/{chatId}/messages", chatId);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void getMessages_UserNotParticipant_ReturnsForbidden() throws Exception {
        // given
        Long chatId = 1L;
        var requestBuilder = MockMvcRequestBuilders.get("/api/private-chats/{chatId}/messages", chatId)
                .with(user(new User("NonParticipant",
                        "NonParticipant@mail.com", "pass", Role.ROLE_USER)));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void createMessage_ValidRequest_ReturnsCreatedMessage() throws Exception {
        // given
        Long chatId = 1L;
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats/{chatId}/messages", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "New message"
                        }""")
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        header().string(HttpHeaders.LOCATION, containsString("http://localhost/api/chats/1/messages/")),
                        content().json("""
                                {
                                  "chatId": 1,
                                  "senderName": "Artem",
                                  "content": "New message",
                                  "editedAt": null
                                }"""),
                        jsonPath("$.id").exists(),
                        jsonPath("$.createdAt").exists()
                );
    }

    @Test
    void createMessage_InvalidRequest_ReturnsBadRequest() throws Exception {
        // given
        Long chatId = 1L;
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats/{chatId}/messages", chatId)
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "   "
                        }""")
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                    errors: ["Контент сообщения не может быть пустым"]
                                }""")
                );
    }

    @Test
    void createMessage_UserNotAuthenticated_ReturnsForbidden() throws Exception {
        // given
        Long chatId = 1L;
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats/{chatId}/messages", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "New message"
                        }""");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void createMessage_UserNotParticipant_ReturnsForbidden() throws Exception {
        // given
        Long chatId = 1L;
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats/{chatId}/messages", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "New message"
                        }""")
                .with(user(new User("NonParticipant",
                        "NonParticipant@mail.com", "pass", Role.ROLE_USER)));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}