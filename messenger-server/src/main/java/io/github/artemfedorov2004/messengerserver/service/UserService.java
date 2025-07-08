package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

public interface UserService {

    User createUser(String username, String email, String password);

    User getByUsername(String username);

    UserDetailsService userDetailsService();

    Page<User> searchUsersExcludingCurrent(String query, User currentUser, Pageable pageable);

    boolean existsAllByUsernames(Collection<String> usernames);
}
