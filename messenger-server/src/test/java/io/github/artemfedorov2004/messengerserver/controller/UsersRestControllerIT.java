package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsersRestControllerIT {

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
    void searchUsers_WithExistingUsername_ReturnsUsersPage() throws Exception {
        // given
        String query = "Pavel";
        int page = 0;
        int size = 5;
        var requestBuilder = MockMvcRequestBuilders.get("/api/users")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                {
                                  "content": [
                                    {
                                      "username": "Pavel",
                                      "email": "pavel@workmail.com"
                                    }
                                  ],
                                  totalElements: 1
                                }""")
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void searchUsers_WithPartialMatch_ReturnsUsersPage() throws Exception {
        // given
        String query = "Pav";
        int page = 0;
        int size = 5;
        var requestBuilder = MockMvcRequestBuilders.get("/api/users")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                {
                                  "content": [
                                    {
                                      "username": "Pavel",
                                      "email": "pavel@workmail.com"
                                    }
                                  ],
                                  totalElements: 1
                                }""")
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void searchUsers_WithEmailQuery_ReturnsUsersPage() throws Exception {
        // given
        String query = "@workmail.com";
        int page = 0;
        int size = 5;
        var requestBuilder = MockMvcRequestBuilders.get("/api/users")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                {
                                  "content": [
                                    {
                                      "username": "Dima",
                                      "email": "dima@workmail.com"
                                    },
                                    {
                                      "username": "Pavel",
                                      "email": "pavel@workmail.com"
                                    }
                                  ],
                                  totalElements: 2
                                }""")
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void searchUsers_WithEmptyQuery_ReturnsEmptyPage() throws Exception {
        // given
        String query = "";
        int page = 0;
        int size = 5;
        var requestBuilder = MockMvcRequestBuilders.get("/api/users")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isEmpty(),
                        jsonPath("$.totalElements").value(0)
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void searchUsers_WithNonExistingQuery_ReturnsEmptyPage() throws Exception {
        // given
        String query = "nonexistent";
        int page = 0;
        int size = 5;
        var requestBuilder = MockMvcRequestBuilders.get("/api/users")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isEmpty(),
                        jsonPath("$.totalElements").value(0)
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void searchUsers_ExcludingCurrentUser_ReturnsEmptyPage() throws Exception {
        // given
        String query = "Artem";
        int page = 0;
        int size = 5;
        var requestBuilder = MockMvcRequestBuilders.get("/api/users")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .with(this.authentication);

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").isEmpty(),
                        jsonPath("$.totalElements").value(0)
                );
    }
}
