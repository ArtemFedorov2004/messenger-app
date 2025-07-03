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
})
class PrivateChatsRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    RequestPostProcessor authentication;

    @BeforeEach
    void setUp() {
        authentication = user(new User(
                "Dima",
                "dima@workmail.com",
                "$2a$10$gX1CW8m2TqS/ckSkoUC12ueKPfWBwYC9HtAg9prF4bxeAaZoO46me",
                Role.ROLE_USER
        ));
    }

    @Test
    void getPrivateChats_ReturnsUserChats() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/private-chats")
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {
                                    "id": 1,
                                    "participantName": "Artem",
                                    "lastMessage": null
                                  }
                                ]""")
                );
    }

    @Test
    void getPrivateChats_UserNotAuthenticated_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.get("/api/private-chats");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void createPrivateChat_ValidRequest_ReturnsCreatedChat() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "participantName": "Pavel"
                        }""")
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        header().string(HttpHeaders.LOCATION, containsString("http://localhost/api/chats/")),
                        content().json("""
                                {
                                  "participantName": "Pavel",
                                  "lastMessage": null
                                }"""),
                        jsonPath("$.id").exists()
                );
    }

    @Test
    void createPrivateChat_InvalidRequest_ReturnsBadRequest() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "participantName": "   "
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
                                    "errors": ["Имя участника не может быть пустым"]
                                }""")
                );
    }

    @Test
    void createPrivateChat_WithSelf_ReturnsBadRequest() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "participantName": "Dima"
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
                                    "detail":"Невозможно создать приватный чат с самим собой"
                                }""")
                );
    }

    @Test
    void createPrivateChat_UserDoesNotExist_ReturnsBadRequest() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "participantName": "NonExistentUser"
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
                                    "detail": "Участники не найдены"
                                }""")
                );
    }

    @Test
    void createPrivateChat_ChatAlreadyExists_ReturnsBadRequest() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "participantName": "Artem"
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
                                    "detail": "Приватный чат уже существует"
                                }""")
                );
    }

    @Test
    void createPrivateChat_UserNotAuthenticated_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/private-chats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "participantName": "Pavel"
                        }""");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isUnauthorized()
                );
    }
}