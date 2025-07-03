package io.github.artemfedorov2004.messengerserver.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void registration_ValidPayload_ReturnsTokensAndSetsCookie() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "Dima",
                            "email": "dima@example.com",
                            "password": "password"
                        }""");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists(),
                        cookie().exists("refreshToken"),
                        cookie().httpOnly("refreshToken", true)
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void registration_ExistingUsername_ReturnsConflict() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "Artem",
                            "email": "mail@example.com",
                            "password": "qazwsx23"
                        }""")
                .locale(Locale.of("ru", "RU"));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                  "type": "about:blank",
                                  "title": "Bad Request",
                                  "status":400,
                                  "detail": "Имя пользователя уже занято",
                                  "instance": "/api/registration"
                                }""")
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void registration_ExistingEmail_ReturnsConflict() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "Vadim",
                            "email": "artem@workmail.com",
                            "password": "qazwsx23"
                        }""")
                .locale(Locale.of("ru", "RU"));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                  "type": "about:blank",
                                  "title": "Bad Request",
                                  "status":400,
                                  "detail": "Адрес электронной почты уже занят",
                                  "instance": "/api/registration"
                                }""")
                );
    }


    @Test
    @Sql("/sql/users.sql")
    void login_ValidCredentials_ReturnsTokensAndSetsCookie() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "Artem",
                            "password": "12345678"
                        }""");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists(),
                        cookie().exists("refreshToken"),
                        cookie().httpOnly("refreshToken", true)
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "Artem",
                            "password": "wrong_password"
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
    @Sql("/sql/users.sql")
    void refresh_WithValidRefreshToken_ReturnsNewTokens() throws Exception {
        // given
        String refreshToken = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "Artem",
                                    "password": "12345678"
                                }"""))
                .andReturn()
                .getResponse()
                .getCookie("refreshToken")
                .getValue();

        var requestBuilder = MockMvcRequestBuilders.post("/api/refresh")
                .cookie(new Cookie("refreshToken", refreshToken));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists(),
                        cookie().exists("refreshToken")
                );
    }

    @Test
    void refresh_WithoutRefreshToken_ReturnsUnauthorized() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/refresh")
                .locale(Locale.of("ru", "RU"));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                  "type": "about:blank",
                                  "title": "Bad Request",
                                  "status": 400,
                                  "detail": "Refresh token не найден",
                                  "instance": "/api/refresh"
                                }""")
                );
    }

    @Test
    @Sql("/sql/users.sql")
    void logout_ClearsRefreshTokenCookie() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/api/logout");

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        cookie().exists("refreshToken"),
                        cookie().maxAge("refreshToken", 0)
                );
    }
}
