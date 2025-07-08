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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerIT {

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
    @Sql("/sql/users.sql")
    void getUser_UserExists_ReturnsUserPayload() throws Exception {
        // given
        String username = "Artem";
        var requestBuilder = MockMvcRequestBuilders.get("/api/users/{username}", username)
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
                                    "username": "Artem",
                                    "email": "artem@workmail.com"
                                }""")
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void getUser_UserNotExists_ReturnsNotFound() throws Exception {
        // given
        String username = "nonexistent";
        var requestBuilder = MockMvcRequestBuilders.get("/api/users/{username}", username)
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                  "type": "about:blank",
                                  "title": "Not Found",
                                  "status": 404,
                                  "detail": "Пользователь не найден",
                                  "instance": "/api/users/nonexistent"
                                }""")
                );
    }
}
