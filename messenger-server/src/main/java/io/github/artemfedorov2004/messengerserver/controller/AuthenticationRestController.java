package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.LoginPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.RegistrationPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.TokensPayload;
import io.github.artemfedorov2004.messengerserver.exception.MissingRefreshTokenCookieException;
import io.github.artemfedorov2004.messengerserver.service.AuthenticationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationRestController {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @Value("${refresh-token.ttl}")
    private int refreshTokenCookieMaxAge;

    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public TokensPayload registration(@RequestBody @Valid RegistrationPayload payload,
                                      BindingResult bindingResult,
                                      HttpServletResponse response) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            TokensPayload tokens = this.authenticationService.registration(payload);
            this.addRefreshTokenCookie(tokens.refreshToken(), this.refreshTokenCookieMaxAge, response);
            return tokens;
        }
    }

    @PostMapping("/login")
    public TokensPayload login(@RequestBody @Valid LoginPayload payload,
                               BindingResult bindingResult,
                               HttpServletResponse response) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            TokensPayload tokens = this.authenticationService.login(payload.username(), payload.password());
            this.addRefreshTokenCookie(tokens.refreshToken(), refreshTokenCookieMaxAge, response);
            return tokens;
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        this.addRefreshTokenCookie(null, 0, response);
    }

    @PostMapping("/refresh")
    public TokensPayload refresh(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() == null) {
            throw new MissingRefreshTokenCookieException("messenger_server.errors.refresh_token_not_found");
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> Objects.equals(cookie.getName(), REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .map(cookie -> {
                    TokensPayload tokens = this.authenticationService.refresh(cookie.getValue());
                    this.addRefreshTokenCookie(tokens.refreshToken(), refreshTokenCookieMaxAge, response);
                    return tokens;
                })
                .orElseThrow(() ->
                        new MissingRefreshTokenCookieException("messenger_server.errors.refresh_token_not_found"));
    }

    private void addRefreshTokenCookie(String refreshToken, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
    }
}
