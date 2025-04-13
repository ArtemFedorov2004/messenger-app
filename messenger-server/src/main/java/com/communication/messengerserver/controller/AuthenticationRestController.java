package com.communication.messengerserver.controller;

import com.communication.messengerserver.controller.payload.LoginPayload;
import com.communication.messengerserver.controller.payload.RegistrationPayload;
import com.communication.messengerserver.controller.payload.TokensPayload;
import com.communication.messengerserver.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationRestController {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    public static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 300;

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
            TokensPayload tokens = this.authenticationService.registration(payload.username(), payload.email(), payload.password());
            this.addRefreshTokenCookie(tokens.refreshToken(), REFRESH_TOKEN_COOKIE_MAX_AGE, response);
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
            this.addRefreshTokenCookie(tokens.refreshToken(), REFRESH_TOKEN_COOKIE_MAX_AGE, response);
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
            throw new NoSuchElementException("Cookies are missing");
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> Objects.equals(cookie.getName(), REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .map(cookie -> {
                    TokensPayload tokens = this.authenticationService.refresh(cookie.getValue());
                    this.addRefreshTokenCookie(tokens.refreshToken(), REFRESH_TOKEN_COOKIE_MAX_AGE, response);
                    return tokens;
                })
                .orElseThrow(() -> new NoSuchElementException("Refresh token not found"));
    }

    private void addRefreshTokenCookie(String refreshToken, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
