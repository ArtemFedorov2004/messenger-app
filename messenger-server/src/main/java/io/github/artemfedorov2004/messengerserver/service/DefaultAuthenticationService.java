package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.controller.payload.RegistrationPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.TokensPayload;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {

    private final UserService userService;

    private final AccessTokenService accessTokenService;

    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    public TokensPayload registration(RegistrationPayload payload) {
        User user = this.userService.createUser(payload.username(), payload.email(), payload.password());

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

        this.refreshTokenService.isTokenValid(refreshToken, user);

        String newAccessToken = this.accessTokenService.generateToken(user);
        String newRefreshToken = this.refreshTokenService.generateToken(user);
        return new TokensPayload(newAccessToken, newRefreshToken);
    }
}
