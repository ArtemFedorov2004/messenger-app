package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    User createUser(String username, String email, String password);

    User getByUsername(String username);

    UserDetailsService userDetailsService();
}
