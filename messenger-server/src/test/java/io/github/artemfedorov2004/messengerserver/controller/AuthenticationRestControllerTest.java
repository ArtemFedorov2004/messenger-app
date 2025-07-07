package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.LoginPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.RegistrationPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.TokensPayload;
import io.github.artemfedorov2004.messengerserver.exception.MissingRefreshTokenCookieException;
import io.github.artemfedorov2004.messengerserver.service.AuthenticationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationRestControllerTest {

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    AuthenticationRestController controller;

    @Test
    void registration_ValidPayload_ReturnsTokensAndSetsCookie() throws BindException {
        // given
        RegistrationPayload payload = new RegistrationPayload("user", "user@example.com", "password");
        TokensPayload tokens = new TokensPayload("accessToken", "refreshToken");
        MapBindingResult bindingResult = new MapBindingResult(Map.of(), "payload");

        doReturn(tokens)
                .when(this.authenticationService).registration(payload);

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        TokensPayload result = this.controller.registration(payload, bindingResult, response);

        // then
        assertEquals(tokens, result);
        assertEquals("refreshToken", response.getCookie(AuthenticationRestController.REFRESH_TOKEN_COOKIE_NAME).getValue());
        verify(this.authenticationService).registration(payload);
        verifyNoMoreInteractions(this.authenticationService);
    }

    @Test
    void registration_InvalidPayload_ThrowsBindException() {
        // given
        RegistrationPayload payload = new RegistrationPayload("", "a@mail.com", "password");
        MapBindingResult bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "username", "Username is required"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        var exception = assertThrows(BindException.class, () ->
                this.controller.registration(payload, bindingResult, response));

        // then
        assertEquals(List.of(new FieldError("payload", "username", "Username is required")),
                exception.getAllErrors());
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    void login_ValidCredentials_ReturnsTokensAndSetsCookie() throws BindException {
        // given
        LoginPayload payload = new LoginPayload("user", "password");
        TokensPayload tokens = new TokensPayload("accessToken", "refreshToken");
        MapBindingResult bindingResult = new MapBindingResult(Map.of(), "payload");

        doReturn(tokens)
                .when(this.authenticationService).login("user", "password");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        TokensPayload result = this.controller.login(payload, bindingResult, response);

        // then
        assertEquals(tokens, result);
        assertEquals("refreshToken", response.getCookie(AuthenticationRestController.REFRESH_TOKEN_COOKIE_NAME).getValue());
        verify(this.authenticationService).login("user", "password");
        verifyNoMoreInteractions(this.authenticationService);
    }

    @Test
    void login_InvalidCredentials_ThrowsBindException() {
        // given
        LoginPayload payload = new LoginPayload("", "");
        MapBindingResult bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "username", "Username is required"));
        bindingResult.addError(new FieldError("payload", "password", "Password is required"));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        var exception = assertThrows(BindException.class, () ->
                this.controller.login(payload, bindingResult, response));

        // then
        assertEquals(List.of(new FieldError("payload", "username", "Username is required"),
                        new FieldError("payload", "password", "Password is required")),
                exception.getAllErrors());
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    void logout_ClearsRefreshTokenCookie() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        this.controller.logout(response);

        // then
        Cookie cookie = response.getCookie(AuthenticationRestController.REFRESH_TOKEN_COOKIE_NAME);
        assertNotNull(cookie);
        assertEquals(0, cookie.getMaxAge());
        assertNull(cookie.getValue());
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    void refresh_WithValidCookie_ReturnsNewTokens() {
        // given
        TokensPayload newTokens = new TokensPayload("newAccess", "newRefresh");
        Cookie refreshCookie = new Cookie(AuthenticationRestController.REFRESH_TOKEN_COOKIE_NAME, "oldRefresh");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(refreshCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();

        doReturn(newTokens)
                .when(this.authenticationService).refresh("oldRefresh");

        // when
        TokensPayload result = this.controller.refresh(request, response);

        // then
        assertEquals(newTokens, result);
        assertEquals("newRefresh", response.getCookie(AuthenticationRestController.REFRESH_TOKEN_COOKIE_NAME).getValue());
        verify(this.authenticationService).refresh("oldRefresh");
        verifyNoMoreInteractions(this.authenticationService);
    }

    @Test
    void refresh_WithoutCookies_ThrowsMissingRefreshTokenCookieException() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        assertThrows(MissingRefreshTokenCookieException.class, () ->
                this.controller.refresh(request, response));

        // then
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    void refresh_WithoutRefreshTokenCookie_ThrowsMissingRefreshTokenCookieException() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("otherCookie", "value"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        assertThrows(MissingRefreshTokenCookieException.class, () ->
                this.controller.refresh(request, response));

        // then
        verifyNoInteractions(this.authenticationService);
    }

    @Test
    void refresh_WithInvalidRefreshTokenCookie_ThrowsJwtException() {
        // given
        Cookie refreshCookie = new Cookie(AuthenticationRestController.REFRESH_TOKEN_COOKIE_NAME, "invalid.token");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(refreshCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();

        doThrow(new JwtException("token is invalid"))
                .when(this.authenticationService).refresh("invalid.token");

        // when
        assertThrows(JwtException.class, () ->
                this.controller.refresh(request, response));

        // then
        verify(this.authenticationService).refresh("invalid.token");
        verifyNoMoreInteractions(this.authenticationService);
    }

    @Test
    void handleJwtException_ReturnsUnauthorized() {
        // given
        var exception = new JwtException("token is invalid");

        // when
        var result = this.controller.handleJwtException(exception);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());

        verifyNoInteractions(this.authenticationService);
    }
}