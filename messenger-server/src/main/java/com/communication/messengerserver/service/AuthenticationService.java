package com.communication.messengerserver.service;

import com.communication.messengerserver.controller.payload.TokensPayload;
import com.communication.messengerserver.entity.User;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    public TokensPayload registration(String username, String email, String password) {
        User user = this.userService.createUser(username, email, password);

        String accessToken = this.accessTokenService.generateToken(user);
        String refreshToken = this.refreshTokenService.generateToken(user);
        return new TokensPayload(accessToken, refreshToken);
    }

    public TokensPayload login(String username, String password) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password
        ));

        var user = this.userService.userDetailsService()
                .loadUserByUsername(username);

        String accessToken = this.accessTokenService.generateToken(user);
        String refreshToken = this.refreshTokenService.generateToken(user);
        return new TokensPayload(accessToken, refreshToken);
    }

    public TokensPayload refresh(String refreshToken) {
        String username = this.refreshTokenService.extractUsername(refreshToken);

        var user = this.userService.userDetailsService()
                .loadUserByUsername(username);

        if (!this.refreshTokenService.isTokenValid(refreshToken, user)) {
            throw new MalformedJwtException("Refresh token is invalid");
        }

        String newAccessToken = this.accessTokenService.generateToken(user);
        String newRefreshToken = this.refreshTokenService.generateToken(user);
        return new TokensPayload(newAccessToken, newRefreshToken);
    }
}
