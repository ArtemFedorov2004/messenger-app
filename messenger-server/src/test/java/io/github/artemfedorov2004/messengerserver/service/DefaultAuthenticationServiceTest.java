package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.controller.payload.RegistrationPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.TokensPayload;
import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.AlreadyExistsException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultAuthenticationServiceTest {

    @Mock
    UserService userService;

    @Mock
    AccessTokenService accessTokenService;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    DefaultAuthenticationService service;

    @Test
    void registration_UserDoesNotExist_ReturnsTokens() {
        // given
        RegistrationPayload payload = new RegistrationPayload(
                "username", "email@example.com", "password");

        User created = new User("username", "email@example.com", "password", Role.ROLE_USER);
        doReturn(created)
                .when(this.userService).createUser("username", "email@example.com", "password");
        doReturn("accessToken")
                .when(this.accessTokenService).generateToken(created);
        doReturn("refreshToken")
                .when(this.refreshTokenService).generateToken(created);

        // when
        TokensPayload result = this.service.registration(payload);

        // then
        assertEquals(new TokensPayload("accessToken", "refreshToken"), result);

        verify(this.userService).createUser(payload.username(), payload.email(), payload.password());
        verify(this.accessTokenService).generateToken(created);
        verify(this.refreshTokenService).generateToken(created);
        verifyNoMoreInteractions(this.userService, this.accessTokenService, this.refreshTokenService);
        verifyNoInteractions(this.authenticationManager);
    }

    @Test
    void registration_WhenUsernameExists_ThrowsException() {
        // given
        RegistrationPayload payload = new RegistrationPayload(
                "username", "username@example.com", "password");

        doThrow(new AlreadyExistsException("Username already exists"))
                .when(this.userService).createUser("username", "username@example.com", "password");

        // when
        assertThrows(AlreadyExistsException.class, () ->
                this.service.registration(payload));

        // then
        verify(this.userService).createUser(payload.username(), payload.email(), payload.password());
        verifyNoMoreInteractions(this.userService);
        verifyNoInteractions(this.accessTokenService, this.refreshTokenService, this.authenticationManager);
    }

    @Test
    void registration_WhenEmailExists_ThrowsException() {
        // given
        RegistrationPayload payload = new RegistrationPayload(
                "username", "username@example.com", "password");

        doThrow(new AlreadyExistsException("Email already exists"))
                .when(this.userService).createUser("username", "username@example.com", "password");

        // when
        assertThrows(AlreadyExistsException.class, () ->
                this.service.registration(payload));

        // then
        verify(this.userService).createUser(payload.username(), payload.email(), payload.password());
        verifyNoMoreInteractions(this.userService);
        verifyNoInteractions(this.accessTokenService, this.refreshTokenService, this.authenticationManager);
    }

    @Test
    void login_UserExists_ReturnsTokens() {
        // given
        String username = "username";
        String password = "password";
        User user = new User("username", "email", "hash", Role.ROLE_USER);

        when(this.userService.userDetailsService())
                .thenReturn(un -> user);
        doReturn("accessToken")
                .when(this.accessTokenService).generateToken(user);
        doReturn("refreshToken")
                .when(this.refreshTokenService).generateToken(user);

        // when
        TokensPayload result = this.service.login(username, password);

        // then
        assertEquals(new TokensPayload("accessToken", "refreshToken"), result);

        verify(this.authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        verify(this.userService).userDetailsService();
        verify(this.accessTokenService).generateToken(user);
        verify(this.refreshTokenService).generateToken(user);
        verifyNoMoreInteractions(this.authenticationManager, this.userService,
                this.accessTokenService, this.refreshTokenService);
    }

    @Test
    void login_WithInvalidCredentials_ThrowsAuthenticationException() {
        // given
        String username = "username";
        String password = "password";

        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(this.authenticationManager).authenticate(
                        new UsernamePasswordAuthenticationToken(username, password));

        // when
        assertThrows(BadCredentialsException.class, () ->
                this.service.login(username, password));

        // then
        verify(this.authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        verifyNoMoreInteractions(this.authenticationManager);
        verifyNoInteractions(this.userService, this.accessTokenService, this.refreshTokenService);
    }

    @Test
    void refresh_WithValidToken_ReturnsNewTokens() {
        // given
        String oldRefreshToken = "valid.refresh.token";
        String username = "username";
        User user = new User(username, "email", "hash", Role.ROLE_USER);

        doReturn(username)
                .when(this.refreshTokenService).extractUsername(oldRefreshToken);
        when(this.userService.userDetailsService())
                .thenReturn(un -> user);
        doReturn(true)
                .when(this.refreshTokenService).isTokenValid(oldRefreshToken, user);
        doReturn("newAccessToken")
                .when(this.accessTokenService).generateToken(user);
        doReturn("newRefreshToken")
                .when(this.refreshTokenService).generateToken(user);

        // when
        TokensPayload result = this.service.refresh(oldRefreshToken);

        // then
        assertEquals(new TokensPayload("newAccessToken", "newRefreshToken"), result);

        verify(this.refreshTokenService).extractUsername(oldRefreshToken);
        verify(this.userService).userDetailsService();
        verify(this.refreshTokenService).isTokenValid(oldRefreshToken, user);
        verify(this.accessTokenService).generateToken(user);
        verify(this.refreshTokenService).generateToken(user);
        verifyNoMoreInteractions(this.userService, this.accessTokenService, this.refreshTokenService);
        verifyNoInteractions(this.authenticationManager);
    }

    @Test
    void refresh_WithInvalidToken_ThrowsJwtException() {
        // given
        String invalidToken = "invalid.token";

        doThrow(new JwtException("token not valid"))
                .when(this.refreshTokenService).extractUsername(invalidToken);

        // when
        assertThrows(JwtException.class, () ->
                this.service.refresh(invalidToken));

        // then
        verify(this.refreshTokenService).extractUsername(invalidToken);
        verifyNoMoreInteractions(this.refreshTokenService);
        verifyNoInteractions(this.userService, this.accessTokenService, this.authenticationManager);
    }
}