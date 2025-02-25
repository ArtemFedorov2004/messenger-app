package com.communication.messengerserver.auth;

import com.communication.messengerserver.user.User;
import com.communication.messengerserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private User registerNewUser(Jwt jwt) {
        String userId = jwt.getSubject();
        Map<String, Object> claims = jwt.getClaims();

        if (!claims.containsKey("given_name")) {
            throw new RuntimeException("User claims do not contain the expected 'given_name'");
        }
        String firstname = String.valueOf(claims.get("given_name"));

        if (!claims.containsKey("family_name")) {
            throw new RuntimeException("User claims do not contain the expected 'family_name'");
        }
        String lastname = String.valueOf(claims.get("family_name"));

        String username = firstname + lastname;

        if (!claims.containsKey("email")) {
            throw new RuntimeException("User claims do not contain the expected 'email'");
        }
        String email = String.valueOf(claims.get("email"));

        User user = User.builder()
                .id(userId)
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .username(username)
                .build();

        return userRepository.save(user);
    }

    public User authenticate(Jwt jwt) {
        String userId = jwt.getSubject();

        return userRepository.findById(userId)
                .orElseGet(() -> registerNewUser(jwt));
    }
}
