package io.github.artemfedorov2004.messengerserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/users.sql")
    void getUser_UserExists_ReturnsUserPayload() throws Exception {
        // given
        String username = "Artem";
        var requestBuilder = MockMvcRequestBuilders.get("/api/users/{username}", username);

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
        var requestBuilder = MockMvcRequestBuilders.get("/api/users/{username}", username);

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
