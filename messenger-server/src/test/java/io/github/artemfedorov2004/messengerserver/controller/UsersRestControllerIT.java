package io.github.artemfedorov2004.messengerserver.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsersRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/sql/users.sql")
    void searchUsers_WithExistingUsername_ReturnsUsersPage() throws Exception {
        // given
        String query = "Artem";
        int page = 0;
        int size = 5;
        var requestBuilder = MockMvcRequestBuilders.get("/api/users")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size));

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
                                      "username": "Artem",
                                      "email": "artem@workmail.com"
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
        String query = "Art";
        int page = 0;
        int size = 5;
        var requestBuilder = MockMvcRequestBuilders.get("/api/users")
                .param("query", query)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size));

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
                                      "username": "Artem",
                                      "email": "artem@workmail.com"
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
                .param("size", String.valueOf(size));

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
                                      "username": "Artem",
                                      "email": "artem@workmail.com"
                                    },
                                    {
                                      "username": "Dima",
                                      "email": "dima@workmail.com"
                                    },
                                    {
                                      "username": "Pavel",
                                      "email": "pavel@workmail.com"
                                    }
                                  ],
                                  totalElements: 3
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
                .param("size", String.valueOf(size));

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
                .param("size", String.valueOf(size));

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
