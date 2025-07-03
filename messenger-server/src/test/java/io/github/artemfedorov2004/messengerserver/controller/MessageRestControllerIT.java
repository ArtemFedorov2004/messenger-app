package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

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
class MessageRestControllerIT {

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
    void updateMessage_ValidRequest_ReturnsUpdatedMessage() throws Exception {
        // given
        Long messageId = 1L;
        var requestBuilder = MockMvcRequestBuilders.patch("/api/private-chat-messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "Updated content"
                        }""")
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
                                  chatId: 1,
                                  senderName: "Artem",
                                  content: "Updated content",
                                  createdAt: "2023-01-01T10:00:00"
                                }"""),
                        jsonPath("$.id").exists(),
                        jsonPath("$.editedAt").exists()
                );
    }

    @Test
    void updateMessage_InvalidRequest_ReturnsBadRequest() throws Exception {
        // given
        Long messageId = 1L;
        var requestBuilder = MockMvcRequestBuilders.patch("/api/private-chat-messages/{messageId}", messageId)
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
                                    "errors": ["Контент сообщения не может быть пустым"]
                                }""")
                );
    }

    @Test
    void updateMessage_UserNotAuthenticated_ReturnsForbidden() throws Exception {
        // given
        Long messageId = 1L;
        var requestBuilder = MockMvcRequestBuilders.patch("/api/private-chat-messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "Updated content"
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
    void updateMessage_UserNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        Long messageId = 2L;
        var requestBuilder = MockMvcRequestBuilders.patch("/api/private-chat-messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "content": "Updated content"
                        }""")
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void deleteMessage_ValidRequest_ReturnsNoContent() throws Exception {
        // given
        Long messageId = 1L;
        var requestBuilder = MockMvcRequestBuilders.delete("/api/private-chat-messages/{messageId}", messageId)
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    void deleteMessage_UserNotAuthenticated_ReturnsForbidden() throws Exception {
        // given
        Long messageId = 1L;
        var requestBuilder = MockMvcRequestBuilders.delete("/api/private-chat-messages/{messageId}", messageId);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void deleteMessage_UserNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        Long messageId = 2L;
        var requestBuilder = MockMvcRequestBuilders.delete("/api/private-chat-messages/{messageId}", messageId)
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}